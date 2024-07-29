FROM python:3.9-alpine

WORKDIR /usr/app
COPY requirements.txt .

RUN apk update && apk add --no-cache \
    build-base \
    libpq \
    libpq-dev \
    gcc

RUN pip install -U --no-cache-dir -r requirements.txt
COPY app.py .
COPY ./src ./src

ENTRYPOINT ["faststream", "run", "app:app", "--log-level", "debug"]