# 数据库归档（learnai）

- 来源：本机 MySQL 8.0.44 上的旧库 `learnai`，2026-10-21 导出后已从实例里删除
- 导出命令：

```
mysqldump --single-transaction --routines --triggers \
  --default-character-set=utf8mb4 --databases learnai > 20261021-learnai.sql
```

- 文件：`20261021-learnai.sql`（64851 字节）
- 源文件 sha256：`1bc5f6033df55a538a56e66331a39463559763c3eb7bed856f7218133d3aecd7`
- 已做回灌验证（导进临时库比表数与关键表行数一致）
