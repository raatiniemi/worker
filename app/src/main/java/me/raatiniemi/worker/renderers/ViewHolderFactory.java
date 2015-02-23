package me.raatiniemi.worker.renderers;

import me.raatiniemi.worker.domain.DomainObject;

public abstract class ViewHolderFactory
{
    public abstract RenderViewHolderBuilder getViewHolderBuilder(DomainObject domainObject);

    public abstract int getViewType(DomainObject domainObject);
}
