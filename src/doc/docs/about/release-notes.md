# {{ gradle.version }} Release Notes

!!! warning 
    The release is completely working, but repositories part was not migrated to the new api
    and use commands api as before (now deprecated). I started repositories refactor  (long ago) 
    but have no time to finish it for now. Releasing current state as "better deprecated apis than nothing". 

    If possible 3.2 compatible versions would be released with current apis
    and then repositories refactor would be done (when I would have time for it).

Release targets orient 3.1 compatibility. No api or behaviour changes.

[Orient 3.1 release notes](https://orientdb.org/docs/3.1.x/release/3.1/What-is-new-in-OrientDB-3.1.html)

Also, see guice-persist-orient [4.0.0 release notes](http://xvik.github.io/guice-persist-orient/4.0.0/about/release-notes/) 
for orient 3 related updates.

!!! note
    Orient docs mention `ODatabaseSession` type for pessimistic locs, but
    required methods are available in `ODatabaseDocument` (and `ODatabaseObject`)
    provided as connections (ofc you can cast document connection to session, but it's not required).

## Known issues

Remains the same from version 4.0.0 (due to not migrated repositories):

- Conversion for new OVertex and OEdge objects not supported
- [Streaming api](https://orientdb.org/docs/3.1.x/java/Java-Query-API.html#streamin-api) not supported
- Live queries unsubscription method might not be called on remote connection in some cases (looks like a bug)
- Functions, executed through object api, might produce incorrect results (with nulls).
  Marker exception would be thrown to indicate this case.
  