FROM python:3.8-alpine

RUN pip install pipenv

# Installing Python deps without a venv (not needed in container).
COPY Pipfile /plugins/wemo/
COPY Pipfile.lock /plugins/wemo/
COPY wemo.py /plugins/wemo/

WORKDIR /plugins/wemo/
RUN pipenv install --system --deploy --ignore-pipfile

WORKDIR /$PROJECT_NAME
ENTRYPOINT ["pipenv", "run", "python", "wemo.py"]
