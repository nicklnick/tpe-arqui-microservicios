FROM node:20

# Node images have a non-root user and group called `node`
# https://github.com/nodejs/docker-node/blob/main/docs/BestPractices.md#non-root-user
RUN mkdir -p /usr/app \
    && chown -R node:node /usr/app
WORKDIR /usr/app
USER node 

COPY --chown=node:node package.json .
COPY --chown=node:node package-lock.json .
RUN npm install

COPY --chown=node:node . .

EXPOSE 3000

ENTRYPOINT [ "npm", "run" ]

CMD [ "start" ]
