const hre = require("hardhat");

const TOKEN_PRESETS = {
  MATS: { name: "Mats Token", decimals: 18, supply: "1000000", liquidity: "10000" },
  IDRX: { name: "IDRX Token", decimals: 18, supply: "1000000000", liquidity: "10000000" },
  USDT: { name: "Tether USD", decimals: 6, supply: "1000000", liquidity: "100000" },
  USDC: { name: "USD Coin", decimals: 6, supply: "1000000", liquidity: "100000" },
  DAI: { name: "Dai Stablecoin", decimals: 18, supply: "1000000", liquidity: "100000" },
  WBTC: { name: "Wrapped Bitcoin", decimals: 8, supply: "1000", liquidity: "10" },
  LINK: { name: "Chainlink", decimals: 18, supply: "1000000", liquidity: "50000" },
  UNI: { name: "Uniswap", decimals: 18, supply: "1000000", liquidity: "50000" },
  AAVE: { name: "Aave", decimals: 18, supply: "1000000", liquidity: "10000" },
  SHIB: { name: "Shiba Inu", decimals: 18, supply: "1000000000000", liquidity: "10000000000" },
  PEPE: { name: "Pepe", decimals: 18, supply: "1000000000000", liquidity: "10000000000" },
  ARB: { name: "Arbitrum", decimals: 18, supply: "1000000", liquidity: "100000" },
  OP: { name: "Optimism", decimals: 18, supply: "1000000", liquidity: "100000" }
};

const DEFAULT_SYMBOLS = Object.keys(TOKEN_PRESETS);

function parseSymbols() {
  const rawSymbols = process.env.TEST_SWAP_TOKENS || DEFAULT_SYMBOLS.join(",");
  return rawSymbols
    .split(",")
    .map((symbol) => symbol.trim().toUpperCase())
    .filter(Boolean);
}

function androidKeys(symbol) {
  if (symbol === "MATS") {
    return {
      token: "MATS_TOKEN_ADDRESS",
      pool: "MATS_SWAP_POOL_ADDRESS",
      decimals: null
    };
  }
  if (symbol === "IDRX") {
    return {
      token: "IDRX_TOKEN_ADDRESS",
      pool: "IDRX_SWAP_POOL_ADDRESS",
      decimals: null
    };
  }
  return {
    token: `SWAP_${symbol}_TOKEN_ADDRESS`,
    pool: `SWAP_${symbol}_POOL_ADDRESS`,
    decimals: `SWAP_${symbol}_DECIMALS`
  };
}

async function main() {
  const [deployer] = await hre.ethers.getSigners();
  const symbols = parseSymbols();
  const ethLiquidity = process.env.TEST_POOL_ETH_LIQUIDITY || "0.005";
  const MockEvmToken = await hre.ethers.getContractFactory("MockEvmToken");
  const MatsSwapPool = await hre.ethers.getContractFactory("MatsSwapPool");
  const localProperties = [];

  console.log("Deploying testnet swap routes from:", deployer.address);
  console.log("Routes:", symbols.join(", "));
  console.log("ETH liquidity per pool:", ethLiquidity, "ETH");
  console.log("");

  for (const symbol of symbols) {
    const preset = TOKEN_PRESETS[symbol];
    if (!preset) {
      throw new Error(`Unknown token symbol: ${symbol}`);
    }

    console.log(`Deploying ${symbol} mock token...`);
    const initialSupply = hre.ethers.parseUnits(preset.supply, preset.decimals);
    const token = await MockEvmToken.deploy(preset.name, symbol, preset.decimals, initialSupply);
    await token.waitForDeployment();
    const tokenAddress = await token.getAddress();

    console.log(`Deploying ${symbol}/ETH pool...`);
    const pool = await MatsSwapPool.deploy(tokenAddress, deployer.address);
    await pool.waitForDeployment();
    const poolAddress = await pool.getAddress();

    const tokenLiquidity = process.env[`TEST_${symbol}_TOKEN_LIQUIDITY`] || preset.liquidity;
    const tokenLiquidityUnits = hre.ethers.parseUnits(tokenLiquidity, preset.decimals);
    const ethLiquidityUnits = hre.ethers.parseEther(ethLiquidity);

    console.log(`Seeding ${symbol}/ETH pool with ${tokenLiquidity} ${symbol} + ${ethLiquidity} ETH...`);
    const approveTx = await token.approve(poolAddress, tokenLiquidityUnits);
    await approveTx.wait();

    const seedTx = await pool.addLiquidity(tokenLiquidityUnits, { value: ethLiquidityUnits });
    await seedTx.wait();

    const keys = androidKeys(symbol);
    localProperties.push(`${keys.token}=${tokenAddress}`);
    localProperties.push(`${keys.pool}=${poolAddress}`);
    if (keys.decimals) {
      localProperties.push(`${keys.decimals}=${preset.decimals}`);
    }

    console.log(`${symbol} token: ${tokenAddress}`);
    console.log(`${symbol} pool:  ${poolAddress}`);
    console.log("");
  }

  console.log("Copy these values into Android local.properties:");
  console.log("");
  console.log(localProperties.join("\n"));
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
