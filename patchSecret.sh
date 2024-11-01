#!/bin/bash

# 순서대로 key, value, secret_name 입력
read -p "Enter the key: " key
read -p "Enter the value: " value
read -p "Enter the secret name: " secret_name

# value 를 base64로 인코딩
encoded_value=$(echo -n "$value" | base64 -w 0)

# Secret 존재 여부 확인
if kubectl get secret $secret_name &> /dev/null; then
    # Secret이 존재하면 패치
    kubectl patch secret $secret_name --type='json' -p="[{\"op\": \"add\", \"path\": \"/data/$key\", \"value\":\"$encoded_value\"}]"
    echo "Secret '$secret_name' has been updated with the new key-value pair."
else
    # Secret이 존재하지 않으면 새로 생성
    kubectl create secret generic $secret_name --from-literal=$key=$value
    echo "New secret '$secret_name' has been created with the key-value pair."
fi

# 변경사항 확인
echo "Verifying the secret:"
kubectl get secret $secret_name -o jsonpath="{.data.$key}" | base64 --decode