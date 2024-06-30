FROM python:3.11

RUN groupadd -g 999 python \
    && useradd -r -u 999 -g python python \
    && mkdir -p /usr/app \
    && python -m venv /usr/app/.venv \
    && chown -R python:python /usr/app 

# As user has no home, pip cannot cache packages so we disable it 
ENV PIP_NO_CACHE_DIR=off
ENV PATH="/usr/app/.venv/bin:$PATH"

WORKDIR /usr/app
USER python

COPY --chown=python:python requirements.txt requirements.txt
RUN pip install --upgrade pip && \
    pip install -r requirements.txt

COPY --chown=python:python . .

EXPOSE 8000

CMD ["uvicorn", "src.app:app", "--host", "0.0.0.0", "--port", "8000"]
