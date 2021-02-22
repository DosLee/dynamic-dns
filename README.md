# 阿里云域名IP动态更新 (DDNS)

> 参考阿里云官方SDK文档编写
> `https://help.aliyun.com/document_detail/177364.html?spm=5176.10695662.1996646101.searchclickresult.17d4316bwBGuA8`

## 需求: 
动态更新域名IP地址

## 下载

## 使用
Docker 命令： `docker run -d --name dns -e aliyun.accessKeyId=access -e aliyun.secret=password -e aliyun.domainNames="example.domain.com" -e aliyun.cron="0 0/5 * * * ?"  --restart=always dynamic-dns:v1.0`
