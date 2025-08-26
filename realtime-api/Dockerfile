FROM node:20-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci
# Source mounted by compose; hot reload via ts-node-dev or nest start --watch
EXPOSE 5000
CMD ["npm","run","start:dev"]
