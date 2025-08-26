FROM node:20-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci
# Source mounted by compose for hot reload
EXPOSE 3200
CMD ["npm","run","dev:docker"]
