FROM python:3.9-alpine

WORKDIR /usr/app

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY app.py .
COPY ./src ./src

RUN apk update && apk add --no-cache \
    build-base \
    libpq \
    libpq-dev \
    gcc

ENTRYPOINT ["python", "app.py"]
