package jason.com.rxremvplib.bean;

import java.util.List;

/** 数组 和 页码
 * Created by jason on 18/9/8.
 */

public class HttpListResponse<T> {

    List<T> select;

    public List<T> getSelect() {
        return select;
    }

    public void setSelect(List<T> select) {
        this.select = select;
    }

    PageCount pagination;

    public PageCount getPagination() {
        return pagination;
    }

    public void setPagination(PageCount pagination) {
        this.pagination = pagination;
    }

    public static class PageCount {
        private int totalCount;

        private int pageCount;

        public int getPageCount() {
            return pageCount;
        }

        public void setPageCount(int pageCount) {
            this.pageCount = pageCount;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }
    }

}
