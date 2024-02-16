package com.testehan.ecommerce.common.order;

public enum OrderStatus {
    NEW {
        @Override
        public String defaultDescription() {
            return "New order placed by the customer";
        }

    },

    CANCELLED {
        @Override
        public String defaultDescription() {
            return "Order was cancelled";
        }
    },

    PROCESSING {
        @Override
        public String defaultDescription() {
            return "Order is being processed";
        }
    },

    PACKAGED {
        @Override
        public String defaultDescription() {
            return "Order is being packaged";
        }
    },

    PICKED {
        @Override
        public String defaultDescription() {
            return "Shipper picked the order";
        }
    },

    SHIPPING {
        @Override
        public String defaultDescription() {
            return "Shipper is delivering the order";
        }
    },

    DELIVERED {
        @Override
        public String defaultDescription() {
            return "Customer received order";
        }
    },

    RETURNED {
        @Override
        public String defaultDescription() {
            return "Order was returned";
        }
    },

    PAID {
        @Override
        public String defaultDescription() {
            return "Customer has paid this order";
        }
    },

    REFUNDED {
        @Override
        public String defaultDescription() {
            return "Customer has been refunded";
        }
    },

    RETURN_REQUESTED {
        @Override
        public String defaultDescription() {
            return "Customer sent request to return order";
        }
    };

    public abstract String defaultDescription();
}
