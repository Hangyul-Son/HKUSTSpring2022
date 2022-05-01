// SPDX-License-Identifier: GPL-3.0
pragma solidity >=0.7.0 <0.9.0;

contract rps {
	address payable private Alice = payable(0xAb8483F64d9C6d1EcF9b849Ae677dD3315835cb2);
	address payable private Bob = payable(0x4B20993Bc481177ec7E8f571ceCaE8A9e22C02db);
	
	bytes32 private aliceHashedChoice;
	bytes32 private bobHashedChoice;
	
	bytes32 public aliceChoice;
	bytes32 public bobChoice;

	bytes32 public Rock = keccak256(abi.encodePacked("Rock"));
	bytes32 public Paper = keccak256(abi.encodePacked("Paper"));
	bytes32 public Scissors = keccak256(abi.encodePacked("Scissors"));

	bool aliceChoiceDeclared;
	bool bobChoiceDeclared;
	bool aliceSentChoice;
	bool bobSentChoice;
	
	uint256 blockDeadline;
	uint256 sendChoiceDeadline;
	bool gameOver;
	
	function sendChoice(bytes32 hashedChoice) public payable { //choice, choice and password concantenated and hashed
		require((msg.sender == Alice && !aliceSentChoice) || (msg.sender == Bob && !bobSentChoice));
		require(msg.value == 1 ether);
		if (msg.sender == Alice) { 
			aliceHashedChoice = hashedChoice; 
			aliceSentChoice = true;
		} else if (msg.sender == Bob) { 
			bobHashedChoice = hashedChoice; 
			bobSentChoice = true;
		}
		//MODIFIED
		sendChoiceDeadline = block.number + 100;
	}

	function reveal(string memory choice, string memory password) public {
		require(aliceSentChoice && bobSentChoice);
		require(msg.sender == Alice || msg.sender == Bob);
		
		if(msg.sender == Alice) {
			require(!aliceChoiceDeclared);
			require(keccak256(abi.encodePacked(choice, password)) == aliceHashedChoice); 
			aliceChoice = keccak256(abi.encodePacked(choice));
			aliceChoiceDeclared = true;
		} 
		else if (msg.sender == Bob) {
			require(!bobChoiceDeclared);
			require(keccak256(abi.encodePacked(choice, password)) == bobHashedChoice);
			bobChoice = keccak256(abi.encodePacked(choice));
			bobChoiceDeclared = true;
		}

		blockDeadline = block.number + 100; //DEADLINE FOR REVEALING CHOICE
	}

	function compare() public { 
		require(!gameOver);
		require(block.number > blockDeadline || (aliceChoiceDeclared && bobChoiceDeclared));

		//Prevent Re-entrancy Attack
		gameOver = true;
		if(aliceChoiceDeclared && !bobChoiceDeclared) {
			Alice.call{value: 2 ether, gas: 2300}("");
			return;
		}
		else if(!aliceChoiceDeclared && bobChoiceDeclared) {
			Bob.call{value: 2 ether, gas: 2300}("");
			return;
		}

		if(aliceChoice == Rock && bobChoice == Scissors || aliceChoice == Paper && bobChoice == Rock || aliceChoice == Scissors && bobChoice == Paper) {
			Alice.call{value: 2 ether, gas: 2300}("");
		}
		else if(aliceChoice == Rock && bobChoice == Paper || aliceChoice == Paper && bobChoice == Scissors || aliceChoice == Scissors && bobChoice == Rock) {
			Bob.call{value: 2 ether, gas: 2300}("");
		}
		else {
			Alice.call{value: 1 ether, gas: 2300}("");
			Bob.call{value: 1 ether, gas: 2300}("");
		}
	}

		//MODIFIED
	function cancelGame() public {
		require(gameOver != true);
		require(msg.sender == Alice || msg.sender == Bob);
		require(!bobSentChoice || !aliceSentChoice);
		require(block.number > sendChoiceDeadline);

		gameOver = true;
		if(!bobSentChoice) {
			Alice.call{value: 1 ether, gas: 2300}("");
		}
		else if(!aliceSentChoice) {
			Bob.call{value: 1 ether, gas: 2300}("");
		}
	}
}