require("@nomicfoundation/hardhat-toolbox");
require("dotenv").config();

const INFURA_PROJECT_ID = process.env.INFURA_PROJECT_ID || "";
const PRIVATE_KEY = process.env.DEPLOYER_PRIVATE_KEY || "";

const sepoliaRpcUrl = INFURA_PROJECT_ID
  ? `https://sepolia.infura.io/v3/${INFURA_PROJECT_ID}`
  : "";

module.exports = {
  solidity: {
    version: "0.8.20",
    settings: {
      optimizer: {
        enabled: true,
        runs: 200
      }
    }
  },
  networks: {
    sepolia: {
      url: sepoliaRpcUrl,
      accounts: PRIVATE_KEY ? [PRIVATE_KEY] : []
    }
  }
};
