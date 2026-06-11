# NLstudy 云服务器部署记录

部署时间：2026-06-09 00:12

## 服务器

- 云厂商：阿里云 ECS
- 系统：Ubuntu 24.04.2 LTS
- 公网 IP：`39.96.3.61`
- 域名：`sper51.icu`、`www.sper51.icu`

## 部署结构

- 前端目录：`/opt/nlstudy/frontend`
- 后端 Jar：`/opt/nlstudy/backend/nlstudy.jar`
- 后端生产配置：`/opt/nlstudy/backend/application-prod.yml`
- Agent 目录：`/opt/nlstudy/learn_agent`
- 数据库脚本：`/opt/nlstudy/sql/exam_platform.sql`
- MySQL 应用密码文件：`/opt/nlstudy/.mysql_app_password`

## 服务

| 服务 | systemd 名称 | 监听地址 | 状态 |
|---|---|---|---|
| Nginx | `nginx` | `0.0.0.0:80` | 已启动 |
| MySQL | `mysql` | `127.0.0.1:3306` | 已启动 |
| Spring Boot | `nlstudy-backend` | `127.0.0.1:8080` | 已启动 |
| Learn Agent | `nlstudy-agent` | `127.0.0.1:5000` | 已启动 |

## 验证结果

- `http://39.96.3.61` 可访问前端。
- `http://39.96.3.61/api/v1/auth/admin/login` 登录成功。
- `admin / 123456`、`wanglaoshi / 123456`、`stu_001 / 123456` 登录接口验证通过。
- 教师端 AI 出题接口通过公网 IP 验证成功。
- 三端冒烟脚本使用 `http://39.96.3.61` 验证通过，无白屏、乱码、API/console 错误。

## 域名状态

域名 `sper51.icu` 和 `www.sper51.icu` 已解析到 `39.96.3.61`，但公网访问时被阿里云返回 ICP 备案拦截页。当前建议：

1. 先使用 `http://39.96.3.61` 访问系统。
2. 完成域名 ICP 备案后，再启用域名访问。
3. 备案完成后再申请 HTTPS 证书并配置 443。

## 运维命令

```bash
systemctl status nlstudy-backend
systemctl status nlstudy-agent
systemctl status nginx
systemctl status mysql

journalctl -u nlstudy-backend -n 100 --no-pager
journalctl -u nlstudy-agent -n 100 --no-pager

systemctl restart nlstudy-backend
systemctl restart nlstudy-agent
systemctl reload nginx
```
