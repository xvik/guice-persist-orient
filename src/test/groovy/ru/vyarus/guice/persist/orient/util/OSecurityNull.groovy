package ru.vyarus.guice.persist.orient.util

import com.orientechnologies.orient.core.db.record.OIdentifiable
import com.orientechnologies.orient.core.id.ORID
import com.orientechnologies.orient.core.metadata.security.ORestrictedOperation
import com.orientechnologies.orient.core.metadata.security.ORole
import com.orientechnologies.orient.core.metadata.security.OSecurity
import com.orientechnologies.orient.core.metadata.security.OSecurityRole
import com.orientechnologies.orient.core.metadata.security.OToken
import com.orientechnologies.orient.core.metadata.security.OUser
import com.orientechnologies.orient.core.record.impl.ODocument

/**
 * @author Vyacheslav Rusakov
 * @since 20.11.2018
 */
class OSecurityNull implements OSecurity {

    @Override
    OUser create() {
        return null
    }

    @Override
    void load() {

    }

    @Override
    boolean isAllowed(Set<OIdentifiable> iAllowAll, Set<OIdentifiable> iAllowOperation) {
        return true
    }

    @Override
    OIdentifiable allowUser(ODocument iDocument, ORestrictedOperation iOperationType, String iUserName) {
        return null
    }

    @Override
    OIdentifiable allowRole(ODocument iDocument, ORestrictedOperation iOperationType, String iRoleName) {
        return null
    }

    @Override
    OIdentifiable denyUser(ODocument iDocument, ORestrictedOperation iOperationType, String iUserName) {
        return null
    }

    @Override
    OIdentifiable denyRole(ODocument iDocument, ORestrictedOperation iOperationType, String iRoleName) {
        return null
    }

    @Override
    OIdentifiable allowUser(ODocument iDocument, String iAllowFieldName, String iUserName) {
        return null
    }

    @Override
    OIdentifiable allowRole(ODocument iDocument, String iAllowFieldName, String iRoleName) {
        return null
    }

    @Override
    OIdentifiable allowIdentity(ODocument iDocument, String iAllowFieldName, OIdentifiable iId) {
        return null
    }

    @Override
    OIdentifiable disallowUser(ODocument iDocument, String iAllowFieldName, String iUserName) {
        return null
    }

    @Override
    OIdentifiable disallowRole(ODocument iDocument, String iAllowFieldName, String iRoleName) {
        return null
    }

    @Override
    OIdentifiable disallowIdentity(ODocument iDocument, String iAllowFieldName, OIdentifiable iId) {
        return null
    }

    @Override
    OUser authenticate(String iUsername, String iUserPassword) {
        return null
    }

    @Override
    OUser authenticate(OToken authToken) {
        return null
    }

    @Override
    OUser getUser(String iUserName) {
        return null
    }

    @Override
    OUser getUser(ORID iUserId) {
        return null
    }

    @Override
    OUser createUser(String iUserName, String iUserPassword, String... iRoles) {
        return null
    }

    @Override
    OUser createUser(String iUserName, String iUserPassword, ORole... iRoles) {
        return null
    }

    @Override
    boolean dropUser(String iUserName) {
        return false
    }

    @Override
    ORole getRole(String iRoleName) {
        return null
    }

    @Override
    ORole getRole(OIdentifiable role) {
        return null
    }

    @Override
    ORole createRole(String iRoleName, OSecurityRole.ALLOW_MODES iAllowMode) {
        return null
    }

    @Override
    ORole createRole(String iRoleName, ORole iParent, OSecurityRole.ALLOW_MODES iAllowMode) {
        return null
    }

    @Override
    boolean dropRole(String iRoleName) {
        return false
    }

    @Override
    List<ODocument> getAllUsers() {
        return null
    }

    @Override
    List<ODocument> getAllRoles() {
        return null
    }

    @Override
    void close(boolean onDelete) {

    }

    @Override
    void createClassTrigger() {

    }

    @Override
    OSecurity getUnderlying() {
        return null
    }

    @Override
    long getVersion() {
        return 0
    }

    @Override
    void incrementVersion() {

    }
}
