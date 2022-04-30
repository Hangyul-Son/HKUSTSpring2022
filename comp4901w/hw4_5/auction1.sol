// SPDX-License-Identifier: GPL-3.0
pragma solidity >=0.7.0 <0.9.0;

contract auction2 {
	address public king;
	uint256 public king_bid;
	uint256 public deposit;
	mapping(address=>bytes32) private bid_amount;
	mapping(address=>bool) private paid;
	uint256 makeBidDeadline;
	uint256 checkBidDeadline;

	constructor(uint256 amount) { 
		king = msg.sender; 
		deposit = amount;
		makeBidDeadline = block.number + 6429; //6429 BLOCKS = 1 DAY
		checkBidDeadline = makeBidDeadline + 6429; 
	}

	function makeBid(bytes32 bid) public payable{ 
		require(makeBidDeadline > block.number);
		require(msg.value == deposit);
    bid_amount[msg.sender] = bid;
  }

	function checkBid(uint256 amount, string memory password) public payable { 
		require(msg.value == amount);
		require(block.number > makeBidDeadline); 
		require(checkBidDeadline > block.number);
		require(keccak256(abi.encodePacked(amount, password)) == bid_amount[msg.sender]);

		//PREVENTS REENTRANCY ATTACK
		require(paid[msg.sender] == false);
		paid[msg.sender] = true; 
		
		//RETURN THE DEPOSIT AMOUNT
		address payable reciever = payable(msg.sender);
		reciever.transfer(deposit);
		
		if(amount > king_bid){ 
			address payable previous_king = payable(king);
			previous_king.transfer(king_bid);
	
			//UPLOAD NEW KING
			king = msg.sender;
			king_bid = amount;	
		}
		else {
			address payable not_king = payable(msg.sender);
			not_king.transfer(amount);
		}
	}
	
	function returnBid(uint256 amount, string memory password) public pure returns (bytes32){
		return keccak256(abi.encodePacked(amount, password));
	}
}