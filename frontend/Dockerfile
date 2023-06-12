# ベースとなるイメージを指定
FROM node:20-alpine3.17

# 作業ディレクトリを指定
WORKDIR /app

# 必要なファイルをコピー
COPY package.json ./
COPY package-lock.json ./

# 必要な依存関係をインストール
RUN npm install

# アプリケーションのソースコードをコピー
COPY . .

# アプリケーションをビルド
RUN npm run build

# コンテナ起動時に実行するコマンド
CMD ["npm", "start"]