FROM tiangolo/uvicorn-gunicorn-fastapi:python3.7

COPY requirements.txt ./

ENV PIP_INDEX_URL=https://pypi.tuna.tsinghua.edu.cn/simple
RUN pip install --no-cache-dir -r requirements.txt --default-timeout=1000

COPY app app
WORKDIR app

ENV TZ Asia/Shanghai

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
