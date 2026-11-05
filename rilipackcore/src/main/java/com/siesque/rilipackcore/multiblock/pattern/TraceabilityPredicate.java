package com.siesque.rilipackcore.multiblock.pattern;

public abstract class TraceabilityPredicate {
    protected final MatchContext matchContext;

    public TraceabilityPredicate() {
        this(new MatchContext());
    }

    public TraceabilityPredicate(MatchContext matchContext) {
        this.matchContext = matchContext;
    }

    public abstract boolean test(IBlockWorldState state);

    public TraceabilityPredicate or(TraceabilityPredicate other) {
        return new OrPredicate(this, other);
    }

    public TraceabilityPredicate and(TraceabilityPredicate other) {
        return new AndPredicate(this, other);
    }

    public MatchContext getMatchContext() {
        return matchContext;
    }

    private static class OrPredicate extends TraceabilityPredicate {
        private final TraceabilityPredicate left;
        private final TraceabilityPredicate right;

        OrPredicate(TraceabilityPredicate left, TraceabilityPredicate right) {
            super();
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean test(IBlockWorldState state) {
            return left.test(state) || right.test(state);
        }
    }

    private static class AndPredicate extends TraceabilityPredicate {
        private final TraceabilityPredicate left;
        private final TraceabilityPredicate right;

        AndPredicate(TraceabilityPredicate left, TraceabilityPredicate right) {
            super();
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean test(IBlockWorldState state) {
            return left.test(state) && right.test(state);
        }
    }
}
