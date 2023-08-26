package com.futurhero.community.bean;

/**
 * 用于分页，其中limit、rows、path需要手动传入，current从请求的参数中获得
 */
public class Page {
    private int current = 1;
    private int limit = 10;
    // 总行数，需要查数据库
    private int rows;
    // 保存请求路径，便于复用
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current > 0) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit > 0 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // 获得当前页的起始行
    public int getOffset() {
        return (current - 1) * limit;
    }

    // 获得总页数
    public int getTotal() {
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    // 获得起始页码
    // 模板中page.from就是调用page对象的getFrom方法
    public int getFrom() {
        return Math.max(current - 2, 1);
    }

    // 获得结束页码
    // 模板中page.to就是调用page对象的getTo方法
    public int getTo() {
        return Math.min(current + 2, getTotal());
    }
}
