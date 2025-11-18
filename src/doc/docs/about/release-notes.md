# {{ gradle.version }} Release Notes

!!! important 
    The release is completely working, but repositories part was not migrated to the new api
    and use commands api as before (now deprecated).

    Manual indexes creation was deprecated (assumed to be completely removed), but 
    index creation extensions are completely working now.

    Overall, orientdb is going to drop object database support (most likely in version 4),
    so all related features will have to be removed. I hope [youtrackdb](https://youtrackdb.io/) 
    would be already released at that time providing new object api (and so most object-related
    features could be ported to a new youtrackdb integration module).

Release targets orientdb 3.2 compatibility and guice 7 (jakarta.inject).   
No api or behavior changes.

[Orient 3.2 release notes](https://orientdb.dev/docs/3.2.x/release/3.2/What-is-new-in-OrientDB-3.2.html))

If migrating from orinetdb 2.x, see guice-persist-orient [4.0.0 release notes](http://xvik.github.io/guice-persist-orient/4.0.0/about/release-notes/) 
for orient 3 related updates.

## Known issues

Remains the same from version 4.0.0 (due to not migrated repositories):

- Conversion for new OVertex and OEdge objects not supported
- [Streaming api](https://orientdb.dev/docs/3.2.x/java/Java-Query-API.html#streamin-api) not supported
- Live queries unsubscription method might not be called on remote connection in some cases (looks like a bug)
- Functions, executed through object api, might produce incorrect results (with nulls).
  Marker exception would be thrown to indicate this case.

Also, there is a known problem that OrientModule is using object db classes and so it's
not possible to use it without orientdb-object jar.

## Migration

Replace `javax.inject` usages with `jakarta.inject`.