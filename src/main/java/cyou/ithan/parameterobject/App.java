package cyou.ithan.parameterobject;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
    public static void main(String[] args) {
        var params = ParameterObject.builder()
                .type("sneakers")
                .sortBy("brand")
                .build();
        log.info(params.toString());
        log.info(new SearchService().search(params));
    }

    @Getter
    @RequiredArgsConstructor
    public enum SortOrder {
        ASC("asc"), DESC("desc");
        private final String value;
    }

    @Getter
    @Setter
    @ToString
    public static class ParameterObject {
        public static final String DEFAULT_SORT_BY = "price";
        public static final SortOrder DEFAULT_SORT_ORDER = SortOrder.ASC;
        private String type;
        private String sortBy = DEFAULT_SORT_BY;
        private SortOrder sortOrder = DEFAULT_SORT_ORDER;

        private ParameterObject(Builder builder) {
            setType(builder.type);
            setSortBy(builder.sortBy != null && !builder.sortBy.isBlank() ? builder.sortBy : sortBy);
            setSortOrder(builder.sortOrder != null ? builder.sortOrder : sortOrder);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private String type;
            private String sortBy;
            private SortOrder sortOrder;

            private Builder() {
            }

            public Builder type(String type) {
                this.type = type;
                return this;
            }

            public Builder sortBy(String sortBy) {
                this.sortBy = sortBy;
                return this;
            }

            public Builder sortOrder(SortOrder sortOrder) {
                this.sortOrder = sortOrder;
                return this;
            }

            public ParameterObject build() {
                return new ParameterObject(this);
            }
        }
    }

    public static class SearchService {
        public String search(String type, String sortBy) {
            return getQuerySummary(type, sortBy, SortOrder.ASC);
        }

        public String search(String type, SortOrder sortOrder) {
            return getQuerySummary(type, "price", sortOrder);
        }

        public String search(ParameterObject parameterObject) {
            return getQuerySummary(parameterObject.getType(), parameterObject.getSortBy(), parameterObject.getSortOrder());
        }

        private String getQuerySummary(String type, String sortBy, SortOrder sortOrder) {
            return String.format("Requesting shoes of type \"%s\" sorted by \"%s\" in \"%sending\" order..", type, sortBy, sortOrder.getValue());
        }
    }
}
