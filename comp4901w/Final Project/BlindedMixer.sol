//Author: SON Hangyul
//SID: 20537267

pragma solidity >=0.7.0 <0.9.0;

//ASSUMPTIONS

//1. N uint128 (LARGE MULTIPLES OF TWO PRIME NUMBERS)
//2. Public Keys uint256
//3. Private Keys uint256
//4. BASE uint120 (ENSURE BASE IS SMALLER THAN N)

//4. EACH BANK IS ASSOCIATED WITH AN UNIQUE PUBLIC KEY
//5. EACH USER ADDRESS CAN SUBMIT ONE SIGNED MESSAGE

contract BlindedMixer {
	mapping(uint256 => mapping(address => uint128)) publicKeyToEncodedMessages; 
	mapping(uint256 => address) publicKeyToBank;
	mapping(uint256 => uint256) publicKeyToDeadline;
	mapping(uint256 => uint256) publicKeyReferenceCount;

	mapping(address => uint128) bankToN;
	mapping(address => uint256) bankToPublicKey; 
	mapping(address => uint256) bankDeposit; 

	mapping(address => uint128) userToSignedMessage;
	mapping(address => bool) userMessageSigned;
	mapping(bytes32 => bool) signedHashUsed;

	constructor() {}
	fallback() external {}

	//USER
	function deposit(uint128 encodedMessage, uint256 publicKey) public payable {
		require(publicKeyToDeadline[publicKey] > block.number);
		require(msg.value == 1 ether);

		publicKeyToEncodedMessages[publicKey][msg.sender] = encodedMessage;
		publicKeyReferenceCount[publicKey] += 1;
	}

	function refundDeposit(uint256 publicKey) public {
		require(publicKeyToDeadline[publicKey] <= block.number);
		require(!userMessageSigned[msg.sender]);

		userMessageSigned[msg.sender] = true;
		payable(msg.sender).call{value: 1 ether, gas:2300}("");
	}

	function withdraw(address receiver, bytes12 nonce, bytes32 signedHash, address bank) public {
		require(publicKeyToDeadline[bankToPublicKey[bank]] <= block.number);
		require(keccak256(abi.encodePacked(receiver, nonce)) == signedHash);
		require(checkSignedHash(signedHash, bank) == true);
		require(signedHashUsed[signedHash] == false);	

		signedHashUsed[signedHash] = true;
		payable(bank).call{value: 1.01 ether, gas:2300}("");
		
		if(msg.sender != receiver){
			payable(receiver).call{value: 0.98 ether, gas:2300}(""); //
			payable(msg.sender).call{value: 0.01 ether, gas:2300}("");
		}
		else {
			payable(receiver).call{value: 0.99 ether, gas:2300}("");
		}
	}


	//BANK
	function sendSignedMessage(uint128 signedMessage, address user) public payable {
		uint256 publicKey = bankToPublicKey[msg.sender];
		require(block.number < publicKeyToDeadline[publicKey]);
		require(msg.value == 1 ether);
		require(fastExponentiation(signedMessage, publicKey, bankToN[msg.sender]) == publicKeyToEncodedMessages[publicKey][user]); 

		userToSignedMessage[user] = signedMessage;
		userMessageSigned[user] = true;
	}

	function registerAsBank(uint256 publicKey, uint128 N) public {
		require(publicKeyToDeadline[publicKey] == 0);

		bankToPublicKey[msg.sender] = publicKey;
		bankToN[msg.sender] = N;

		publicKeyToBank[publicKey] = msg.sender;
		publicKeyToDeadline[publicKey] = block.number + (6400 * 7);
	 }

	//UTILS
	function checkSignedHash(bytes32 signedHash, address bank) view internal returns (bool) {
		uint128 N = bankToN[bank];
		uint256 publicKey = bankToPublicKey[bank];
		uint120 base = uint120(uint256(signedHash));  //WHY NOT UINT128? PREVENT BASE FROM BEING MULTIPLES OF N

		uint120 result = uint120(fastExponentiation(base, publicKey, N));
		return result == base;
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
}