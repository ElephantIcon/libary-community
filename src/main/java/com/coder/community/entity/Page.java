package com.coder.community.entity;

/**
 * 封装分页相关的 信息。
 */
public class Page {

    // 当前的页码
    private int current = 1;
    // 显示上限
    private int limit = 10;
    // 数据总量（计算总页数）
    private int rows;
    // 查询路径（复用分页的链接）
    private String path;


    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >=1 && limit <=100) {
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

    /**
     * 获取当前页的起始行
     * @return
     */
    public int getOffset() {
        // current * limit - limit
        return (current - 1) * limit;
    }

    /**
     * 获取总的页数
     */
    public int getTotal() {
        // rows / limit [+1]
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    /**
     * 获取起始页码,前两页
     */
    public int getFrom() {
        int from = current - 2;
        return Math.max(from, 1);
    }

    /**
     * 获取结束页码
     */
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        // return to > total ? total : to;
        return Math.min(to, total);
    }
}