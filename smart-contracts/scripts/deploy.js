const hre = require("hardhat");

async function main() {
  const name = process.env.TOKEN_NAME || "Mats Token";
  const symbol = process.env.TOKEN_SYMBOL || "MATS";
  const supply = process.env.TOKEN_INITIAL_SUPPLY || "0";

  const decimals = 18;
  const initialSupply = hre.ethers.parseUnits(supply, decimals);

  const MatsToken = await hre.ethers.getContractFactory("MatsToken");
  const token = await MatsToken.deploy(name, symbol, initialSupply);
  await token.waitForDeployment();

  console.log("MatsToken deployed to:", await token.getAddress());
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
