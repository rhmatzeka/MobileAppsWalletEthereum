# Mats Wallet (Android)

Ethereum e-wallet modern berbasis Java + Material 3 untuk latihan Web3 di Android.  
Fitur utama: onboarding, PIN + biometrik, saldo ETH, candlestick chart, kirim/terima via QR, history transaksi, ERC-20 token dasar, dan settings lengkap.

## Tech Stack
- Java 11, Android SDK 36
- Material Design 3, RecyclerView, ViewPager2, Lottie
- Web3j 4.9.x + Infura RPC
- Retrofit + Gson, CoinGecko
- Room + EncryptedSharedPreferences + Android Keystore

## Setup Android
1. Buka project di Android Studio.
2. Isi `local.properties` (root project):
```
INFURA_PROJECT_ID=xxx
ETHERSCAN_API_KEY=xxx
```
3. Sync Gradle dan run aplikasi.

## Smart Contract (ERC-20 untuk testing)
Folder `smart-contracts` sudah disiapkan dengan Hardhat + ERC-20.

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
```

Deploy:
```
npm run deploy:sepolia
```

## Catatan Keamanan
- Jangan commit `local.properties` dan `.env`.
- Jangan bagikan private key.

## License
Untuk kebutuhan tugas/portfolio.
