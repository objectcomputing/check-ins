import React, { useEffect, useContext, useState } from "react";

import EditPermissionsPageRoles from "./EditPermissionsPageRoles";

import { getPermissionsList } from "../api/permissions";

import { AppContext } from "../context/AppContext";
import { selectPermissions } from "../context/selectors";

import { TextField } from "@mui/material";

import "./EditPermissionsPage.css";

const EditPermissionsPage = (props) => {
  // const { state, dispatch } = useContext(AppContext);
  const { state } = useContext(AppContext);
  const { csrf } = state;
  const [permissionsList, setPermissionsList] = useState([]);

  const [searchText, setSearchText] = useState("");

  const [showAllPermissions, setShowAllPermissions] = useState(true);

  const [
    createFeedbackRequestPermissionsAdmin,
    setCreateFeedbackRequestPermissionsAdmin,
  ] = useState(false);
  const [
    createFeedbackRequestPermissionsPDL,
    setCreateFeedbackRequestPermissionsPDL,
  ] = useState(false);
  const [
    createFeedbackRequestPermissionsMember,
    setCreateFeedbackRequestPermissionsMember,
  ] = useState(false);

  const [
    deleteFeedbackRequestPermissionsAdmin,
    setDeleteFeedbackRequestPermissionsAdmin,
  ] = useState(false);
  const [
    deleteFeedbackRequestPermissionsPDL,
    setDeleteFeedbackRequestPermissionsPDL,
  ] = useState(false);
  const [
    deleteFeedbackRequestPermissionsMember,
    setDeleteFeedbackRequestPermissionsMember,
  ] = useState(false);

  const [
    viewFeedbackRequestPermissionsAdmin,
    setViewFeedbackRequestPermissionsAdmin,
  ] = useState(false);
  const [
    viewFeedbackRequestPermissionsPDL,
    setViewFeedbackRequestPermissionsPDL,
  ] = useState(false);
  const [
    viewFeedbackRequestPermissionsMember,
    setViewFeedbackRequestPermissionsMember,
  ] = useState(false);

  const [
    viewFeedbackAnswerPermissionsAdmin,
    setViewFeedbackAnswerPermissionsAdmin,
  ] = useState(false);
  const [
    viewFeedbackAnswerPermissionsPDL,
    setViewFeedbackAnswerPermissionsPDL,
  ] = useState(false);
  const [
    viewFeedbackAnswerPermissionsMember,
    setViewFeedbackAnswerPermissionsMember,
  ] = useState(false);

  const [
    createOrgMembersPermissionsAdmin,
    setCreateOrgMembersPermissionsAdmin,
  ] = useState(false);
  const [createOrgMembersPermissionsPDL, setCreateOrgMembersPermissionsPDL] =
    useState(false);
  const [
    createOrgMembersPermissionsMember,
    setCreateOrgMembersPermissionsMember,
  ] = useState(false);

  const [
    deleteOrgMembersPermissionsAdmin,
    setDeleteOrgMembersPermissionsAdmin,
  ] = useState(false);
  const [deleteOrgMembersPermissionsPDL, setDeleteOrgMembersPermissionsPDL] =
    useState(false);
  const [
    deleteOrgMembersPermissionsMember,
    setDeleteOrgMembersPermissionsMember,
  ] = useState(false);

  const [viewRolePermissionsAdmin, setViewRolePermissionsAdmin] =
    useState(false);
  const [viewRolePermissionsPDL, setViewRolePermissionsPDL] = useState(false);
  const [viewRolePermissionsMember, setViewRolePermissionsMember] =
    useState(false);

  const [assignRolePermissionsAdmin, setAssignRolePermissionsAdmin] =
    useState(false);
  const [assignRolePermissionsPDL, setAssignRolePermissionsPDL] =
    useState(false);
  const [assignRolePermissionsMember, setAssignRolePermissionsMember] =
    useState(false);

  const [viewPermissionsAdmin, setViewPermissionsAdmin] = useState(false);
  const [viewPermissionsPDL, setViewPermissionsPDL] = useState(false);
  const [viewPermissionsMember, setViewPermissionsMember] = useState(false);

  const [viewSkillsReportsAdmin, setViewSkillsReportsAdmin] = useState(false);
  const [viewSkillsReportsPDL, setViewSkillsReportsPDL] = useState(false);
  const [viewSkillsReportsMember, setViewSkillsReportsMember] = useState(false);

  const [viewRetentionReportsAdmin, setViewRetentionReportsAdmin] =
    useState(false);
  const [viewRetentionReportsPDL, setViewRetentionReportsPDL] = useState(false);
  const [viewRetentionReportsMember, setViewRetentionReportsMember] =
    useState(false);

  const [viewAnniversaryReportsAdmin, setViewAnniversaryReportsAdmin] =
    useState(false);
  const [viewAnniversaryReportsPDL, setViewAnniversaryReportsPDL] =
    useState(false);
  const [viewAnniversaryReportsMember, setViewAnniversaryReportsMember] =
    useState(false);

  const [viewBirthdayReportsAdmin, setViewBirthdayReportsAdmin] =
    useState(false);
  const [viewBirthdayReportsPDL, setViewBirthdayReportsPDL] = useState(false);
  const [viewBirthdayReportsMember, setViewBirthdayReportsMember] =
    useState(false);

  const [viewProfileReportsAdmin, setViewProfileReportsAdmin] = useState(false);
  const [viewProfileReportsPDL, setViewProfileReportsPDL] = useState(false);
  const [viewProfileReportsMember, setViewProfileReportsMember] =
    useState(false);

  const [updateCheckins, setUpdateCheckins] = useState(false);
  const [createCheckins, setCreateCheckins] = useState(false);
  const [viewCheckins, setViewCheckins] = useState(false);

  const allPermissions = selectPermissions(state);

  const handleClick = () => setShowAllPermissions(!showAllPermissions);

  const handleClickCreateFeedbackRequestAdmin = () =>
    setCreateFeedbackRequestPermissionsAdmin(
      !createFeedbackRequestPermissionsAdmin
    );

  const handleClickCreateFeedbackRequestPDL = () =>
    setCreateFeedbackRequestPermissionsPDL(
      !createFeedbackRequestPermissionsPDL
    );

  const handleClickCreateFeedbackRequestMember = () =>
    setCreateFeedbackRequestPermissionsMember(
      !createFeedbackRequestPermissionsMember
    );

  const handleClickDeleteFeedbackRequestAdmin = () =>
    setDeleteFeedbackRequestPermissionsAdmin(
      !deleteFeedbackRequestPermissionsAdmin
    );
  const handleClickDeleteFeedbackRequestPDL = () =>
    setDeleteFeedbackRequestPermissionsPDL(
      !deleteFeedbackRequestPermissionsPDL
    );
  const handleClickDeleteFeedbackRequestMember = () =>
    setDeleteFeedbackRequestPermissionsMember(
      !deleteFeedbackRequestPermissionsMember
    );

  const handleClickViewFeedbackRequestAdmin = () =>
    setViewFeedbackRequestPermissionsAdmin(
      !viewFeedbackRequestPermissionsAdmin
    );
  const handleClickViewFeedbackRequestPDL = () =>
    setViewFeedbackRequestPermissionsPDL(!viewFeedbackRequestPermissionsPDL);
  const handleClickViewFeedbackRequestMember = () =>
    setViewFeedbackRequestPermissionsMember(
      !viewFeedbackRequestPermissionsMember
    );

  const handleClickViewFeedbackAnswerAdmin = () =>
    setViewFeedbackAnswerPermissionsAdmin(!viewFeedbackAnswerPermissionsAdmin);
  const handleClickViewFeedbackAnswerPDL = () =>
    setViewFeedbackAnswerPermissionsPDL(!viewFeedbackAnswerPermissionsPDL);
  const handleClickViewFeedbackAnswerMember = () =>
    setViewFeedbackAnswerPermissionsMember(
      !viewFeedbackAnswerPermissionsMember
    );

  const handleClickCreateOrgMembersPermissionsAdmin = () =>
    setCreateOrgMembersPermissionsAdmin(!createOrgMembersPermissionsAdmin);
  const handleClickCreateOrgMembersPermissionsPDL = () =>
    setCreateOrgMembersPermissionsPDL(!createOrgMembersPermissionsPDL);
  const handleClickCreateOrgMembersPermissionsMember = () =>
    setCreateOrgMembersPermissionsMember(!createOrgMembersPermissionsMember);

  const handleClickDeleteOrgMembersPermissionsAdmin = () =>
    setDeleteOrgMembersPermissionsAdmin(!deleteOrgMembersPermissionsAdmin);
  const handleClickDeleteOrgMembersPermissionsPDL = () =>
    setDeleteOrgMembersPermissionsPDL(!deleteOrgMembersPermissionsPDL);
  const handleClickDeleteOrgMembersPermissionsMember = () =>
    setDeleteOrgMembersPermissionsMember(!deleteOrgMembersPermissionsMember);

  const handleClickRolePermissionsViewAdmin = () =>
    setViewRolePermissionsAdmin(!viewRolePermissionsAdmin);
  const handleClickRolePermissionsViewPDL = () =>
    setViewRolePermissionsPDL(!viewRolePermissionsPDL);
  const handleClickRolePermissionsViewMember = () =>
    setViewRolePermissionsMember(!viewRolePermissionsMember);

  const handleClickRolePermissionsAssignAdmin = () =>
    setAssignRolePermissionsAdmin(!assignRolePermissionsAdmin);
  const handleClickRolePermissionsAssignPDL = () =>
    setAssignRolePermissionsPDL(!assignRolePermissionsPDL);
  const handleClickRolePermissionsAssignMember = () =>
    setAssignRolePermissionsMember(!assignRolePermissionsMember);

  const handleClickViewAdmin = () =>
    setViewPermissionsAdmin(!viewPermissionsAdmin);
  const handleClickViewPDL = () => setViewPermissionsPDL(!viewPermissionsPDL);
  const handleClickViewMember = () =>
    setViewPermissionsMember(!viewPermissionsMember);

  const handleClickViewSkillsReportsAdmin = () =>
    setViewSkillsReportsAdmin(!viewSkillsReportsAdmin);
  const handleClickViewSkillsReportsPDL = () =>
    setViewSkillsReportsPDL(!viewSkillsReportsPDL);
  const handleClickViewSkillsReportsMember = () =>
    setViewSkillsReportsMember(!viewSkillsReportsMember);

  const handleClickViewRetentionReportsAdmin = () =>
    setViewRetentionReportsAdmin(!viewRetentionReportsAdmin);
  const handleClickViewRetentionReportsPDL = () =>
    setViewRetentionReportsPDL(!viewRetentionReportsPDL);
  const handleClickViewRetentionReportsMember = () =>
    setViewRetentionReportsMember(!viewRetentionReportsMember);

  const handleClickViewAnniversaryReportsAdmin = () =>
    setViewAnniversaryReportsAdmin(!viewAnniversaryReportsAdmin);
  const handleClickViewAnniversaryReportsPDL = () =>
    setViewAnniversaryReportsPDL(!viewAnniversaryReportsPDL);
  const handleClickViewAnniversaryReportsMember = () =>
    setViewAnniversaryReportsMember(!viewAnniversaryReportsMember);

  const handleClickViewBirthdayReportsAdmin = () =>
    setViewBirthdayReportsAdmin(!viewBirthdayReportsAdmin);
  const handleClickViewBirthdayReportsPDL = () =>
    setViewBirthdayReportsPDL(!viewBirthdayReportsPDL);
  const handleClickViewBirthdayReportsMember = () =>
    setViewBirthdayReportsMember(!viewBirthdayReportsMember);

  const handleClickViewProfileReportsAdmin = () =>
    setViewProfileReportsAdmin(!viewProfileReportsAdmin);
  const handleClickViewProfileReportsPDL = () =>
    setViewProfileReportsPDL(!viewProfileReportsPDL);
  const handleClickViewProfileReportsMember = () =>
    setViewProfileReportsMember(!viewProfileReportsMember);

  const handleClickUpdateCheckins = () => setUpdateCheckins(!updateCheckins);
  const handleClickCreateCheckins = () => setCreateCheckins(!createCheckins);
  const handleClickViewCheckins = () => setViewCheckins(!viewCheckins);

  // const permissionTypes = [
  //   'CAN_VIEW_FEEDBACK_REQUEST',
  //   'CAN_CREATE_FEEDBACK_REQUEST',
  //   'CAN_DELETE_FEEDBACK_REQUEST',
  //   'CAN_VIEW_FEEDBACK_ANSWER',
  //   'CAN_DELETE_ORGANIZATION_MEMBERS',
  //   'CAN_CREATE_ORGANIZATION_MEMBERS',
  //   'CAN_VIEW_ROLE_PERMISSIONS',
  //   'CAN_ASSIGN_ROLE_PERMISSIONS',
  //   'CAN_VIEW_PERMISSIONS',
  //   'CAN_VIEW_SKILLS_REPORT',
  //   'CAN_VIEW_RETENTION_REPORT',
  //   'CAN_VIEW_ANNIVERSARY_REPORT',
  //   'CAN_VIEW_BIRTHDAY_REPORT',
  //   'CAN_VIEW_PROFILE_REPORT',
  //   'CAN_CREATE_CHECKINS',
  //   'CAN_VIEW_CHECKINS',
  //   'CAN_UPDATE_CHECKINS',
  // ];

  useEffect(() => {
    const doTask = async (team) => {
      let res = await getPermissionsList(team, csrf);
      let data =
        res.payload && res.payload.data && !res.error ? res.payload.data : null;
      if (data) {
        setPermissionsList(data);
      }
    };

    if (csrf) {
      doTask();
    }
  }, [permissionsList, csrf]);

  // useEffect(() => {
  //   console.log("Permissions");
  //   console.log(allPermissions, permissionsList);
  // }, [allPermissions, permissionsList]);

  return (
    <div className="edit-permissions-page">
      <div className="search">
        <div>
          <TextField
            label="Search Permissions"
            placeholder="Permission"
            fullWidth={true}
            value={searchText}
            onChange={(e) => {
              setSearchText(e.target.value);
            }}
          />
          <div className="show-all-permissions">
            <input
              onClick={handleClick}
              id="all-permissions"
              type="checkbox"
              value={showAllPermissions}
            />
            <label htmlFor="all-permissions">Show all permissions</label>
          </div>
        </div>
      </div>

      <div className="permissions-list">
        <h2>Edit Feedback Request Permissions Below:</h2>

        <EditPermissionsPageRoles
          title="Create Feedback Request permissions"
          selectAdmin={handleClickCreateFeedbackRequestAdmin}
          admin={createFeedbackRequestPermissionsAdmin}
          selectPDL={handleClickCreateFeedbackRequestPDL}
          pdl={createFeedbackRequestPermissionsPDL}
          selectMember={handleClickCreateFeedbackRequestMember}
          member={createFeedbackRequestPermissionsMember}
        />

        <EditPermissionsPageRoles
          title="Delete Feedback Request permissions"
          selectAdmin={handleClickDeleteFeedbackRequestAdmin}
          admin={deleteFeedbackRequestPermissionsAdmin}
          selectPDL={handleClickDeleteFeedbackRequestPDL}
          pdl={deleteFeedbackRequestPermissionsPDL}
          selectMember={handleClickDeleteFeedbackRequestMember}
          member={deleteFeedbackRequestPermissionsMember}
        />

        <EditPermissionsPageRoles
          title="View Feedback Request permissions"
          selectAdmin={handleClickViewFeedbackRequestAdmin}
          admin={viewFeedbackRequestPermissionsAdmin}
          selectPDL={handleClickViewFeedbackRequestPDL}
          pdl={viewFeedbackRequestPermissionsPDL}
          selectMember={handleClickViewFeedbackRequestMember}
          member={viewFeedbackRequestPermissionsMember}
        />
      </div>

      <div className="permissions-list">
        <h2>Edit Feedback Answer Permissions Below:</h2>
        <EditPermissionsPageRoles
          title="View Feedback Answer permissions"
          selectAdmin={handleClickViewFeedbackAnswerAdmin}
          admin={viewFeedbackAnswerPermissionsAdmin}
          selectPDL={handleClickViewFeedbackAnswerPDL}
          pdl={viewFeedbackAnswerPermissionsPDL}
          selectMember={handleClickViewFeedbackAnswerMember}
          member={viewFeedbackAnswerPermissionsMember}
        />
      </div>

      <div className="permissions-list">
        <h2>Edit Organization Members Permissions Below:</h2>
        <EditPermissionsPageRoles
          title="Create Organization Members permissions"
          selectAdmin={handleClickCreateOrgMembersPermissionsAdmin}
          admin={createOrgMembersPermissionsAdmin}
          selectPDL={handleClickCreateOrgMembersPermissionsPDL}
          pdl={createOrgMembersPermissionsPDL}
          selectMember={handleClickCreateOrgMembersPermissionsMember}
          member={createOrgMembersPermissionsMember}
        />
        <EditPermissionsPageRoles
          title="Delete Organization Members permissions"
          selectAdmin={handleClickDeleteOrgMembersPermissionsAdmin}
          admin={deleteOrgMembersPermissionsAdmin}
          selectPDL={handleClickDeleteOrgMembersPermissionsPDL}
          pdl={deleteOrgMembersPermissionsPDL}
          selectMember={handleClickDeleteOrgMembersPermissionsMember}
          member={deleteOrgMembersPermissionsMember}
        />
      </div>

      <div className="permissions-list">
        <h2>Edit Role Permissions Below:</h2>
        <EditPermissionsPageRoles
          title="View Role permissions"
          selectAdmin={handleClickRolePermissionsViewAdmin}
          admin={viewRolePermissionsAdmin}
          selectPDL={handleClickRolePermissionsViewPDL}
          pdl={viewRolePermissionsPDL}
          selectMember={handleClickRolePermissionsViewMember}
          member={viewRolePermissionsMember}
        />

        <EditPermissionsPageRoles
          title="Assign Role permissions"
          selectAdmin={handleClickRolePermissionsAssignAdmin}
          admin={assignRolePermissionsAdmin}
          selectPDL={handleClickRolePermissionsAssignPDL}
          pdl={assignRolePermissionsPDL}
          selectMember={handleClickRolePermissionsAssignMember}
          member={assignRolePermissionsMember}
        />
      </div>

      <div className="permissions-list">
        <h2>Edit View Permissions Below:</h2>
        <EditPermissionsPageRoles
          title="View permissions"
          selectAdmin={handleClickViewAdmin}
          admin={viewPermissionsAdmin}
          selectPDL={handleClickViewPDL}
          pdl={viewPermissionsPDL}
          selectMember={handleClickViewMember}
          member={viewPermissionsMember}
        />
      </div>

      <div className="permissions-list">
        <h2>Edit View Reports Permissions Below:</h2>
        <EditPermissionsPageRoles
          title="View Skills Reports"
          selectAdmin={handleClickViewSkillsReportsAdmin}
          admin={viewSkillsReportsAdmin}
          selectPDL={handleClickViewSkillsReportsPDL}
          pdl={viewSkillsReportsPDL}
          selectMember={handleClickViewSkillsReportsMember}
          member={viewSkillsReportsMember}
        />
        <EditPermissionsPageRoles
          title="View Retention Reports"
          selectAdmin={handleClickViewRetentionReportsAdmin}
          admin={viewRetentionReportsAdmin}
          selectPDL={handleClickViewRetentionReportsPDL}
          pdl={viewRetentionReportsPDL}
          selectMember={handleClickViewRetentionReportsMember}
          member={viewRetentionReportsMember}
        />
        <EditPermissionsPageRoles
          title="View Anniversary Reports"
          selectAdmin={handleClickViewAnniversaryReportsAdmin}
          admin={viewAnniversaryReportsAdmin}
          selectPDL={handleClickViewAnniversaryReportsPDL}
          pdl={viewAnniversaryReportsPDL}
          selectMember={handleClickViewAnniversaryReportsMember}
          member={viewAnniversaryReportsMember}
        />
        <EditPermissionsPageRoles
          title="View Birthday Reports"
          selectAdmin={handleClickViewBirthdayReportsAdmin}
          admin={viewBirthdayReportsAdmin}
          selectPDL={handleClickViewBirthdayReportsPDL}
          pdl={viewBirthdayReportsPDL}
          selectMember={handleClickViewBirthdayReportsMember}
          member={viewBirthdayReportsMember}
        />
        <EditPermissionsPageRoles
          title="View Profile Reports"
          selectAdmin={handleClickViewProfileReportsAdmin}
          admin={viewProfileReportsAdmin}
          selectPDL={handleClickViewProfileReportsPDL}
          pdl={viewProfileReportsPDL}
          selectMember={handleClickViewProfileReportsMember}
          member={viewProfileReportsMember}
        />
      </div>

      <div className="permissions-list">
        <h2>Edit Checkins Below:</h2>
        <div className="permissions">
          <input
            onClick={handleClickUpdateCheckins}
            id="update-checkins"
            type="checkbox"
            value={updateCheckins}
          />
          <label htmlFor="update-checkins">Update Checkins</label>
        </div>
        <div className="permissions">
          <input
            onClick={handleClickCreateCheckins}
            id="create-checkins"
            type="checkbox"
            value={createCheckins}
          />
          <label htmlFor="create-checkins">Create Checkins</label>
        </div>
        <div className="permissions">
          <input
            onClick={handleClickViewCheckins}
            id="view-checkins"
            type="checkbox"
            value={viewCheckins}
          />
          <label htmlFor="view-checkins">View Checkins</label>
        </div>
      </div>
    </div>
  );
};

export default EditPermissionsPage;
