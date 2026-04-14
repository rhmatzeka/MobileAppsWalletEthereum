# Mats Wallet Smart Contracts (Sepolia)

This folder contains a simple ERC-20 token for testing Mats Wallet.

## Requirements
- Node.js 18+ and npm
- An Infura Project ID
- A Sepolia wallet private key with test ETH for gas

## Setup
1. Install deps:
   - `npm install`
2. Create `.env` based on `.env.example`:
   - `INFURA_PROJECT_ID=...`
   - `DEPLOYER_PRIVATE_KEY=...` (no `0x` prefix)

## Compile
`npm run compile`

## Deploy to Sepolia
`npm run deploy:sepolia`

After deploy, copy the printed contract address and add it to Mats Wallet as a token contract.
