//Author: SON Hangyul

pragma solidity >=0.7.0 <0.9.0;

contract BlindedMixer {
	//Maps public key to 'submittedHashes' 
	//Already have been initialized to a dynamic array
	mapping(bytes => mapping(address => bytes32[])) publicKeyToHashes; 
	mapping(bytes => uint256) publicKeyReferenceCount;
	
	mapping(bytes => address) publicKeyToBank;
	mapping(address => bytes) bankToPublicKeys; //All public keys
	mapping(address => uint256) bankDeposit; 
	mapping(address => bytes32) userToSignedHash;
	
	
	
	uint256 depositDeadline;

	constructor() {
		//ASSUME 1 DAY 6400 BLOCK
		depositDeadline = block.number + (6400 * 7);
	}

	fallback() external {}
//User
	function deposit(bytes32 encodedHash, address bank) public payable {
		require(block.number < depositDeadline);
		require(msg.value == 1 ether);
		publicKeyToHashes[bankToPublicKeys[bank]][msg.sender].push(encodedHash);
		publicKeyReferenceCount[bankToPublicKeys[bank]]++;
	}

	function getSignedHash() public returns (bytes32) {
		return userToSignedHash[msg.sender];
	}

	function withdraw(address receiver, bytes12 nonce, bytes32 signedHash, uint8 v, bytes32 r, bytes32 s) public {
		require(block.number > depositDeadline);
		address bank = ecrecover(signedHash, v, r, s);
		require(bank != address(0));

		payable(msg.sender).transfer(0.01 ether);

	}

	function sendSignedHash(bytes32 signedHash, address user) public payable {
		require(block.number < depositDeadline);
		require(msg.value == 1 ether);
		bankDeposit[msg.sender] += 1;
		userToSignedHash[user] = signedHash;
	}

	//Decentralization
	function registerAsBank(bytes memory publicKey) public {
		 bankToPublicKeys[msg.sender] = publicKey;
	 }
	//Extra Credit User Can Call Withdraw without any deposit
}