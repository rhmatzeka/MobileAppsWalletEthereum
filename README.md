# Ethernest

Ethernest adalah aplikasi Android e-wallet multi-chain berbasis Java dengan tampilan modern, keamanan lokal, integrasi Web3, swap token testnet, NFT, custom RPC EVM, dan flow pembelian ETH melalui backend Midtrans.

## Fitur
- Onboarding wallet, import wallet, PIN login, dan biometric-ready flow.
- Dukungan network EVM preset dan custom RPC: Ethereum, Sepolia, BSC, Avalanche, Polygon, Arbitrum, Optimism, Base, dan Fantom.
- Saldo ETH, token ERC-20, NFT ERC-721/ERC-1155, chart candlestick, serta histori transaksi.
- Home asset list multi-chain dengan icon coin real untuk ETH, BNB, AVAX, Polygon, dan token EVM populer.
- Kirim dan terima ETH dengan QR, set amount, share address, dan deposit from exchange.
- Swap real untuk MATS, IDRX, preset token EVM populer, dan slot token tambahan yang sudah punya pool on-chain.
- UI swap memakai kartu `From/To`, picker token custom, status pool aktif/butuh pool, dan icon token yang sesuai.
- Buy ETH dengan halaman pembayaran in-app WebView, backend Midtrans, dan harga ETH/IDR realtime.

## Tech Stack
- Java 11, Android SDK 36
- Material Design 3, RecyclerView, ViewPager2, WebView
- Web3j 4.9.x + Infura RPC
- Retrofit + Gson, CoinGecko, Etherscan
- Room + EncryptedSharedPreferences + Android Keystore
- Hardhat, Solidity, Express.js, Midtrans

## Setup Android
1. Buka project di Android Studio.
2. Isi `local.properties` (root project):
```
INFURA_PROJECT_ID=xxx
BSC_RPC_URL=https://bsc-dataseed.bnbchain.org
AVALANCHE_RPC_URL=https://api.avax.network/ext/bc/C/rpc
POLYGON_RPC_URL=https://polygon.drpc.org
ETHERSCAN_API_KEY=xxx
MATS_TOKEN_ADDRESS=xxx
MATS_SWAP_POOL_ADDRESS=xxx
IDRX_TOKEN_ADDRESS=xxx
IDRX_SWAP_POOL_ADDRESS=xxx
SWAP_USDT_TOKEN_ADDRESS=
SWAP_USDT_POOL_ADDRESS=
SWAP_USDT_DECIMALS=6
SWAP_USDC_TOKEN_ADDRESS=
SWAP_USDC_POOL_ADDRESS=
SWAP_USDC_DECIMALS=6
SWAP_TOKEN_1_NAME=Example Token
SWAP_TOKEN_1_SYMBOL=EXT
SWAP_TOKEN_1_ADDRESS=xxx
SWAP_TOKEN_1_POOL_ADDRESS=xxx
SWAP_TOKEN_1_DECIMALS=18
BUY_BACKEND_BASE_URL=http://10.0.2.2:8787/
MIDTRANS_PAYMENT_URL=
```
3. Sync Gradle dan run aplikasi.

Catatan:
- Emulator Android memakai `http://10.0.2.2:8787/`.
- HP fisik memakai IP laptop/VPS, contoh `http://192.168.1.10:8787/`.
- Saat development via USB, kamu bisa menjalankan `adb reverse tcp:8787 tcp:8787`.
- Flow Buy di emulator sekarang memprioritaskan `10.0.2.2` lebih dulu supaya dev checkout lokal tidak nyasar ke IP lama.
- Dialog pilih network, custom RPC, dan picker token swap sudah memakai custom modal style agar konsisten dengan UI wallet.

## Buy Server + Midtrans
Flow beli tidak menyimpan private key di Android. Aplikasi membuat order ke backend, backend membuat transaksi Midtrans, pembayaran ditampilkan di dalam aplikasi via WebView, lalu backend mengirim ETH ke wallet setelah status pembayaran berhasil.

Harga ETH/IDR dihitung server-side secara realtime:
- CoinGecko ETH/IDR
- Indodax ETH/IDR
- Binance ETHUSDT + kurs USD/IDR sebagai fallback realtime

```
cd smart-contracts
npm install
copy .env.example .env
npm run start:buy-server
```

Isi `.env` backend:
```
MIDTRANS_SERVER_KEY=xxx
MIDTRANS_IS_PRODUCTION=false
INFURA_PROJECT_ID=xxx
BUY_TREASURY_PRIVATE_KEY=private_key_treasury_testnet_tanpa_0x
BUY_DEV_MODE=true
```

Endpoint penting:
```
GET  /health
GET  /api/price/eth-idr
POST /api/buy/eth
POST /api/midtrans/notification
```

Quick check lokal:
```
http://127.0.0.1:8787/health
```

Untuk production, jalankan buy server di VPS/domain HTTPS dan pasang URL webhook Midtrans ke:
```
https://domain-kamu.com/api/midtrans/notification
```

## Smart Contract
Folder `smart-contracts` berisi ERC-20 token dan pool swap testnet.

Catatan swap:
- Route swap on-chain aktif mengikuti pool yang kamu deploy, default-nya `MATS` dan `IDRX`.
- Preset token populer EVM yang tampil di picker: `USDT`, `USDC`, `DAI`, `WBTC`, `LINK`, `UNI`, `AAVE`, `SHIB`, `PEPE`, `ARB`, dan `OP`.
- Token tambahan bisa dipakai real swap setelah punya token address, pool address, dan liquidity. Isi preset `SWAP_USDT_*`/`SWAP_USDC_*`/dst atau slot `SWAP_TOKEN_1..12` di `local.properties`.
- Token tanpa pool tetap tampil sebagai `Butuh pool`, tetapi tombol swap dikunci sampai address pool dan liquidity tersedia.

Langkah cepat:
```
cd smart-contracts
npm install
copy .env.example .env
```
Isi `.env`:
```
INFURA_PROJECT_ID=xxx
DEPLOYER_PRIVATE_KEY=xxx (tanpa 0x)
TOKEN_NAME=Mats Token
TOKEN_SYMBOL=MATS
TOKEN_INITIAL_SUPPLY=1000000
POOL_TOKEN_SYMBOL=MATS
POOL_TOKEN_ADDRESS=your_token_address_for_pool
POOL_SWAP_ADDRESS=your_pool_address_for_seed
```

Compile dan deploy:
```
npm run compile
npm run deploy:token
npm run deploy:pool
npm run seed:pool
```

Untuk deploy pool token lain, isi `POOL_TOKEN_SYMBOL` dan `POOL_TOKEN_ADDRESS`, jalankan `npm run deploy:pool`, lalu salin output `SWAP_TOKEN_1_*` ke `local.properties`. Setelah pool terisi liquidity via `npm run seed:pool`, swap token itu bisa dipakai dari aplikasi.

Flow testnet paling cepat:
```
cd smart-contracts
copy .env.example .env
```
Isi minimal:
```
INFURA_PROJECT_ID=xxx
DEPLOYER_PRIVATE_KEY=private_key_wallet_testnet_tanpa_0x
TEST_SWAP_TOKENS=MATS,IDRX,USDT,USDC,DAI,WBTC,LINK,UNI,AAVE,SHIB,PEPE,ARB,OP
TEST_POOL_ETH_LIQUIDITY=0.005
```
Pastikan wallet deployer punya Sepolia ETH dari faucet, lalu jalankan:
```
npm install
npm run compile
npm run deploy:testnet-routes
```
Script ini deploy mock ERC-20, deploy pool ETH/token, seed liquidity, lalu mencetak baris `local.properties` untuk Android. Salin output itu ke `local.properties`, sync Gradle, lalu jalankan app. Kalau route sudah aktif, status token berubah dari `Butuh pool` menjadi `Aktif`, dan token swap yang sudah dikonfigurasi ikut tampil di Home `Aset Dimiliki`.

## Keamanan
- Private key user disimpan terenkripsi di Android Keystore/EncryptedSharedPreferences.
- Private key treasury buy server hanya boleh berada di backend `.env`, tidak boleh dimasukkan ke APK.
- `local.properties` dan `smart-contracts/.env` di-ignore dari Git agar secret tidak ikut ter-push.

