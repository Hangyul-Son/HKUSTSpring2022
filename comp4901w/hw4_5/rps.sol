// SPDX-License-Identifier: GPL-3.0
pragma solidity >=0.7.0 <0.9.0;

contract rps {
	address payable private Alice = payable(0xAb8483F64d9C6d1EcF9b849Ae677dD3315835cb2);
	bytes32 private AliceChoice = "";
	string public A_Choice = "";
	address payable private Bob = payable(0x4B20993Bc481177ec7E8f571ceCaE8A9e22C02db);
	bytes32 private BobChoice = "";
	string public B_Choice = "";

	bool aliceChoiceDeclared;
	bool bobChoiceDeclared;
	bool aliceSentChoice;
	bool bobSentChoice;
	
	uint256 blockDeadline;
	bool gameOver;
	
	function sendChoice(bytes32 hashed_choice) public payable { //choice, choice and password concantenated and hashed
		require((msg.sender == Alice && aliceSentChoice == false) || (msg.sender == Bob && bobSentChoice == false));
		require(msg.value == 1 ether);
		if (msg.sender == Alice) { 
			AliceChoice = hashed_choice; 
			aliceSentChoice = true;
		} else if (msg.sender == Bob) { 
			BobChoice = hashed_choice; 
			bobSentChoice = true;
		}
	}

	function reveal(string memory choice, string memory password) public {
		require(aliceSentChoice && bobSentChoice);
		require(msg.sender == Alice || msg.sender == Bob);
		if(msg.sender == Alice) {
			require(keccak256(abi.encodePacked(choice, password)) == AliceChoice); 
			require(!aliceChoiceDeclared);
			A_Choice = choice;
			aliceChoiceDeclared = true;
		} else if (msg.sender == Bob) {
			require(keccak256(abi.encodePacked(choice, password)) == BobChoice);
			require(!bobChoiceDeclared);
			B_Choice = choice; 
			bobChoiceDeclared = true;
		}
		blockDeadline = block.number + 100; //DEADLINE FOR REVEALING CHOICE
	}

	function compare() public { 
		require(!gameOver);
		require(block.number > blockDeadline || (aliceChoiceDeclared && bobChoiceDeclared));
		if(!aliceChoiceDeclared && bobChoiceDeclared) {
			Bob.transfer(2 ether);
			gameOver = true;
			return;
		}
		else if(aliceChoiceDeclared && !bobChoiceDeclared) {
			Alice.transfer(2 ether);
			gameOver = true;
			return;
		}
		//Alice and Bob both have have made the choice
		if(keccak256(abi.encodePacked(A_Choice)) == keccak256(abi.encodePacked(B_Choice))) {
			Alice.transfer(1 ether);
			Bob.transfer(1 ether);
		}
		else if(keccak256(abi.encodePacked(A_Choice)) == keccak256(abi.encodePacked("Rock")) && keccak256(abi.encodePacked(B_Choice)) == keccak256(abi.encodePacked("Paper"))) {
			Bob.transfer(2 ether);
		}
		else if(keccak256(abi.encodePacked(A_Choice)) == keccak256(abi.encodePacked("Rock")) && keccak256(abi.encodePacked(B_Choice)) == keccak256(abi.encodePacked("Scissors"))) {
			Alice.transfer(2 ether);
		}
		else if(keccak256(abi.encodePacked(A_Choice)) == keccak256(abi.encodePacked("Paper")) && keccak256(abi.encodePacked(B_Choice)) == keccak256(abi.encodePacked("Rock"))) {
			Alice.transfer(2 ether);
		}
		else if(keccak256(abi.encodePacked(A_Choice)) == keccak256(abi.encodePacked("Paper")) && keccak256(abi.encodePacked(B_Choice)) == keccak256(abi.encodePacked("Scissors"))) {
			Bob.transfer(2 ether);
		}
		else if(keccak256(abi.encodePacked(A_Choice)) == keccak256(abi.encodePacked("Scissors")) && keccak256(abi.encodePacked(B_Choice)) == keccak256(abi.encodePacked("Rock"))) {
			Bob.transfer(2 ether);
		}
		else if(keccak256(abi.encodePacked(A_Choice)) == keccak256(abi.encodePacked("Scissors")) && keccak256(abi.encodePacked(B_Choice)) == keccak256(abi.encodePacked("Paper"))) {
			Alice.transfer(2 ether);
		}
		else { //When either the choice of Alice or Bob is invalid.
			Alice.transfer(1 ether);
			Bob.transfer(1 ether);
		}
		gameOver = true;
	}
}