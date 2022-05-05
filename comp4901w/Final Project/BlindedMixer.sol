pragma solidity >=0.7.0 <0.9.0;

//Contract
contract BlindedMixer {
	mapping(address => bytes32) registeredHashes;
	mapping(address => uint256) publicKeys;
	mapping(uint256 => mapping(address => address)) publicKeyHashes;
	mapping(address => address) lenderToUser;
	mapping(address => address) UserToLender;

//User
	function deposit(bytes32 encodedHash) public payable{
		//Must Declare Which public key to use
	}

	function withdraw(bytes32 signedHash, bytes32 originalHash, ) public {

	}

//Bank
	function getUnsignedHash() public {
		//
	}

	function sendSignedHash(bytes32 signedHash, address user) public {
		//
	}

	 function registerAsBank(uint256 publicKey) public {
		 //
	 }

	//Extra Credit User Can Call Withdraw without any deposit

	//People
	function lendToUser(address user) {

	}
	
}