sudo pip install pipenv
mkdir -p ../../temp/wemo
cp wemo.py ../../temp/wemo/
cd ../../temp/wemo
ENV PIPENV_VENV_IN_PROJECT "enabled"
pipenv install
