// SPDX-License-Identifier: GPL-3.0
pragma solidity >=0.7.0 <0.9.0;

contract auction2 {
	address payable public king;
	uint256 public kingBid;
	mapping(address=>uint256) private depositAmount;
	mapping(address=>bytes32) private hashedBidAmount;
	mapping(address=>bool) private paid;
	uint256 makeBidDeadline;
	uint256 checkBidDeadline;

	constructor() { 
		makeBidDeadline = block.number + 6429;
		checkBidDeadline = makeBidDeadline + 6429; 
	}

	function makeBid(bytes32 bid) public payable{ 
		require(makeBidDeadline > block.number);
		depositAmount[msg.sender] = msg.value;
    hashedBidAmount[msg.sender] = bid;
  }

	function checkBid(uint256 amount, uint256 password) public payable { 
		require(block.number > makeBidDeadline); 
		require(checkBidDeadline > block.number);
		require(keccak256(abi.encodePacked(amount, password)) == hashedBidAmount[msg.sender]);
		require(depositAmount[msg.sender] >= amount);
		require(!paid[msg.sender]);
		
		paid[msg.sender] = true;
		if(amount > kingBid){ 
			address payable previousKing = king;
			previousKing.call{value:kingBid, gas: 2300}("");

			king = payable(msg.sender);
			uint256 returnDeposit = depositAmount[msg.sender]-amount;
			king.call{value:returnDeposit, gas: 2300}("");
			kingBid = amount;	
		}
		else {
		 	payable(msg.sender).call{value:depositAmount[msg.sender], gas: 2300}("");
		}
	}

	event Winner(address winner, uint256 amount);
	function revealWinner() public returns (bytes32){
		require(block.number > checkBidDeadline);
		emit Winner(king, kingBid);
	}

	function returnBid(uint256 amount, uint256 password) public pure returns (bytes32){
		return keccak256(abi.encodePacked(amount, password));
	}
}