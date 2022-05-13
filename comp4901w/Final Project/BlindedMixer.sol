//Author: SON Hangyul
//SID: 20537267

pragma solidity >=0.7.0 <0.9.0;

//ASSUMPTIONS

//1. N uint128 (LARGE MULTIPLES OF TWO PRIME NUMBERS)
//2. Public Keys uint256
//3. Private Keys uint256
//4. BASE uint128 (ENSURE BASE IS SMALLER THAN N, BASE IS NOT DIVISIBLE BY N). 

//4. EACH BANK IS ASSOCIATED WITH AN UNIQUE PUBLIC KEY
//5. EACH USER ADDRESS CAN SUBMIT ONE SIGNED MESSAGE
//6. User submitted encoded messages can not be  '0'

contract BlindedMixer {
	mapping(uint256 => mapping(address => uint128)) publicKeyToEncodedMessages; 
	mapping(uint256 => mapping(uint128 => address)) publicKeyToAddress;
	mapping(uint256 => address) publicKeyToBank;
	mapping(uint256 => uint128) publicKeyToN;
	mapping(uint256 => uint256) publicKeyToDepositDeadline;
	mapping(uint256 => uint256) publicKeyToWithdrawDeadline;

	mapping(uint256 => uint256) publicKeyReferenceCount;
	mapping(uint256 => uint256) publicKeyWithdrawCount;

	mapping(address => uint256) bankToPublicKey; 
	mapping(address => uint256) bankDeposit; 

	mapping(address => uint128) userToSignedMessage;
	mapping(address => bool) userMessageSigned;
	mapping(uint128 => bool) signedMessageUsed;

	constructor() {}
	fallback() external {}

	//---------------------------------------USER-----------------------------------------------
	function deposit(uint128 encodedMessage, uint256 publicKey) public payable {
		require(publicKeyToDepositDeadline[publicKey] > block.number);
		require(encodedMessage != 0); 
		require(msg.value == 1 ether);

		publicKeyToEncodedMessages[publicKey][msg.sender] = encodedMessage;
		publicKeyToAddress[publicKey][encodedMessage] = msg.sender;
		publicKeyReferenceCount[publicKey] += 1;
	}

	function refundUserDeposit(uint128 encodedMessage, uint256 publicKey) public {
		require(publicKeyToDepositDeadline[publicKey] <= block.number);
		require(publicKeyToAddress[publicKey][encodedMessage] == msg.sender);
		require(!userMessageSigned[msg.sender]);

		userMessageSigned[msg.sender] = true;
		payable(msg.sender).call{value: 1 ether, gas:2300}("");
	}

	function withdraw(address receiver, uint64 nonce, uint128 signedMessage, address bank) public {
		uint256 publicKey = bankToPublicKey[bank];
		require(publicKeyToDepositDeadline[publicKey] <= block.number);
		require(publicKeyToWithdrawDeadline[publicKey] > block.number);

		bytes32 originalHash = keccak256(abi.encodePacked(receiver, nonce));
		require(validateSignedMessage(originalHash, signedMessage, bank));
		require(!signedMessageUsed[signedMessage]);	

		publicKeyWithdrawCount[publicKey] += 1;
		signedMessageUsed[signedMessage] = true;
		
		payable(bank).call{value: 0.01 ether, gas:2300}("");

		if(msg.sender != receiver){
			payable(receiver).call{value: 0.98 ether, gas:2300}("");
			payable(msg.sender).call{value: 0.01 ether, gas:2300}("");
		}
		else {
			payable(receiver).call{value: 0.99 ether, gas:2300}("");
		}
	}

	//---------------------------------------BANK-----------------------------------------------
	function submitSignedMessage(uint128 signedMessage, address user) public payable {
		uint256 publicKey = bankToPublicKey[msg.sender];	
		require(publicKeyToDepositDeadline[publicKey] > block.number);
		require(msg.value == 1 ether);
	
		uint128 encodedMessage = uint128(fastExponentiation(signedMessage, publicKey, publicKeyToN[publicKey]));
		require(publicKeyToAddress[publicKey][encodedMessage] == user);
		require(!userMessageSigned[user]);

		bankDeposit[msg.sender] += msg.value;
		userToSignedMessage[user] = signedMessage;
		userMessageSigned[user] = true;
	}

	function refundBankDeposit() public {
		uint256 publicKey = bankToPublicKey[msg.sender];
		require(publicKeyToWithdrawDeadline[publicKey] <= block.number);
		require(publicKeyReferenceCount[publicKey] >= publicKeyWithdrawCount[publicKey]);
		
	  uint256 deposit = bankDeposit[msg.sender]; 
		bankDeposit[msg.sender] = 0;
		payable(msg.sender).call{value: deposit, gas:2300}("");
	}

	function registerAsBank(uint256 publicKey, uint128 N) public {
		require(publicKey != 0);
		require(publicKeyToDepositDeadline[publicKey] == 0);
		require(bankToPublicKey[msg.sender] == 0);

		bankToPublicKey[msg.sender] = publicKey;
		publicKeyToN[publicKey] = N;

		publicKeyToBank[publicKey] = msg.sender;
		publicKeyToDepositDeadline[publicKey] = block.number + (6400 * 7);
		publicKeyToWithdrawDeadline[publicKey] = block.number + (6400 * 14);
	 }

	//---------------------------------------UTILS-----------------------------------------------
	function validateSignedMessage(bytes32 originalHash, uint128 signedMessage, address bank) view internal returns (bool) {
		uint256 publicKey = bankToPublicKey[bank];
		uint128 N = publicKeyToN[publicKey];
		uint128 originalMessage = uint128(uint256(originalHash));  
		uint128 result = uint128(fastExponentiation(signedMessage, publicKey, N)); //4. SHOULD PREVENT BASE FROM BEING LARGER THAN N
		return result == originalMessage;
	}

	function fastExponentiation(uint128 base, uint256 exponent, uint128 N) internal pure returns (uint256) {
		uint256 result = 1;
		uint256 base_ = base; 
		while (exponent > 0) {
			if (exponent % 2 == 1) {
				result = (result * base_) % N;
				exponent = exponent - 1; 
			}
			else {
				base_ = (base_ * base_) % N;
				exponent = exponent / 2;
			}
		}
		return result;
	}

	//---------------------------------------DUBUG-UTILS--------------------------------------------
	function getMessage(address receiver, uint128 nonce) view public returns (uint128) {
		return uint128(uint256(keccak256(abi.encodePacked(receiver, nonce))));
	}
}