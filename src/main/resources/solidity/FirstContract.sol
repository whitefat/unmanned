// SPDX-License-Identifier: UNLICENSED
pragma solidity ^0.8.0;

contract FirstContract {

    uint256 number;
    uint public qtyCups;

    /**
     * @dev Store value in variable
     * @param num value to store
     */
    function store(uint256 num) public {
        number = num;
    }

    /**
     * @dev Return value
     * @return value of 'number'
     */
    function retrieve() public view returns (uint256){
        return number;
    }

    // Get the current quantity
    function get() public view returns (uint) {
        return qtyCups;
    }

    // Increment quantity by 1
    function increment() public {
        qtyCups += 1; // same as  qtyCups = qtyCups + 1;
    }

    // Function to decrement count by 1
    function decrement() public {
        qtyCups -= 1; // same as  qtyCups = qtyCups - 1;
        // What happens if qtyCups = 0 when this func is called?
    }

}
