import os

VERSION = "v0.0.16"
IMAGE_NAME = 'customer-landing'
DOCKER_SERVER = "722740969534.dkr.ecr.cn-northwest-1.amazonaws.com.cn"

print('[1] Login in AWS ecr')
assert os.system(
    f'aws ecr get-login-password --region cn-northwest-1 | \
    docker login --username AWS --password-stdin https://{DOCKER_SERVER}'
) == 0

print('[2] Build docker image version', VERSION)
assert os.system(
    f'docker build --platform=linux/amd64 -t {DOCKER_SERVER}/{IMAGE_NAME}:{VERSION} .'
) == 0

print('[3] Push docker image version', VERSION)
assert os.system(
    f'docker push {DOCKER_SERVER}/{IMAGE_NAME}:{VERSION}'
) == 0

print('Success!')



