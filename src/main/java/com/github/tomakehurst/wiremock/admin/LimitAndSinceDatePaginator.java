package com.github.tomakehurst.wiremock.admin;

import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.stubbing.ServedStub;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import java.util.Date;
import java.util.List;

import static com.github.tomakehurst.wiremock.admin.model.Conversions.toDate;
import static com.github.tomakehurst.wiremock.admin.model.Conversions.toInt;
import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkArgument;

public class LimitAndSinceDatePaginator implements Paginator<ServedStub> {

    private final List<ServedStub> source;
    private final Integer limit;
    private final Date since;

    public LimitAndSinceDatePaginator(List<ServedStub> source, Integer limit, Date since) {
        checkArgument(limit == null || limit >= 0, "limit must be 0 or greater");
        this.source = source;
        this.limit = limit;
        this.since = since;
    }

    public static LimitAndSinceDatePaginator fromRequest(List<ServedStub> source, Request request) {
        return new LimitAndSinceDatePaginator(
            source,
            toInt(request.queryParameter("limit")),
            toDate(request.queryParameter("since"))
        );
    }

    @Override
    public List<ServedStub> select() {
        FluentIterable<ServedStub> chain = FluentIterable.from(source);
        return chain.filter(new Predicate<ServedStub>() {
            @Override
            public boolean apply(ServedStub input) {
                return since == null ||
                    input.getRequest().getLoggedDate().after(since);
            }
        })
        .limit(
            firstNonNull(limit, source.size())
        )
        .toList();
    }
    
    @Override
    public int getTotal() {
        return source.size();
    }
}
