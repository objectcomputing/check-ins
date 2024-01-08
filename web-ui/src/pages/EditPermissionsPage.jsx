import React, { useEffect, useContext, useState } from "react";

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
    updateFeedbackRequestPermissions,
    setUpdateFeedbackRequestPermissions,
  ] = useState(false);
  const [
    deleteFeedbackRequestPermissions,
    setDeleteFeedbackRequestPermissions,
  ] = useState(false);
  const [viewFeedbackRequestPermissions, setViewFeedbackRequestPermissions] =
    useState(false);

  const [viewFeedbackAnswerPermissions, setViewFeedbackAnswerPermissions] =
    useState(false);

  const [updateOrgMembersPermissions, setUpdateOrgMembersPermissions] =
    useState(false);
  const [deleteOrgMembersPermissions, setDeleteOrgMembersPermissions] =
    useState(false);

  const [viewRolePermissions, setViewRolePermissions] = useState(false);
  const [assignRolePermissions, setAssignRolePermissions] = useState(false);

  const [viewPermissions, setViewPermissions] = useState(false);

  const [viewSkillsReports, setViewSkillsReports] = useState(false);
  const [viewRetentionReports, setViewRetentionReports] = useState(false);
  const [viewAnniversaryReports, setViewAnniversaryReports] = useState(false);
  const [viewBirthdayReports, setViewBirthdayReports] = useState(false);
  const [viewProfileReports, setViewProfileReports] = useState(false);

  const [updateCheckins, setUpdateCheckins] = useState(false);
  const [createCheckins, setCreateCheckins] = useState(false);
  const [viewCheckins, setViewCheckins] = useState(false);

  const allPermissions = selectPermissions(state);

  const handleClick = () => setShowAllPermissions(!showAllPermissions);

  const handleClickUpdateFeedbackRequest = () =>
    setUpdateFeedbackRequestPermissions(!updateFeedbackRequestPermissions);
  const handleClickDeleteFeedbackRequest = () =>
    setDeleteFeedbackRequestPermissions(!deleteFeedbackRequestPermissions);
  const handleClickViewFeedbackRequest = () =>
    setViewFeedbackRequestPermissions(!viewFeedbackRequestPermissions);

  const handleClickViewFeedbackAnswer = () =>
    setViewFeedbackAnswerPermissions(!viewFeedbackAnswerPermissions);

  const handleClickUpdateOrgMembersPermissions = () =>
    setUpdateOrgMembersPermissions(!updateOrgMembersPermissions);
  const handleClickDeleteOrgMembers = () =>
    setDeleteOrgMembersPermissions(!deleteOrgMembersPermissions);

  const handleClickRolePermissionsView = () =>
    setViewRolePermissions(!viewRolePermissions);
  const handleClickRolePermissionsAssign = () =>
    setAssignRolePermissions(!assignRolePermissions);

  const handleClickView = () => setViewPermissions(!viewPermissions);

  const handleClickViewSkillsReports = () =>
    setViewSkillsReports(!viewSkillsReports);
  const handleClickViewRetentionReports = () =>
    setViewRetentionReports(!viewRetentionReports);
  const handleClickViewAnniversaryReports = () =>
    setViewAnniversaryReports(!viewAnniversaryReports);
  const handleClickViewBirthdayReports = () =>
    setViewBirthdayReports(!viewBirthdayReports);
  const handleClickViewProfileReports = () =>
    setViewProfileReports(!viewProfileReports);

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

  useEffect(() => {
    console.log("Permissions");
    console.log(allPermissions, permissionsList);
  }, [allPermissions, permissionsList]);

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
        <div className="permissions">
          <input
            onClick={handleClickUpdateFeedbackRequest}
            id="update-feedback-request-permissions"
            type="checkbox"
            value={updateFeedbackRequestPermissions}
          />
          <label htmlFor="update-feedback-request-permissions">
            Update Feedback Request permissions
          </label>
        </div>
        <div className="permissions">
          <input
            onClick={handleClickDeleteFeedbackRequest}
            id="delete-feedback-request-permissions"
            type="checkbox"
            value={deleteFeedbackRequestPermissions}
          />
          <label htmlFor="delete-feedback-request-permissions">
            Delete Feedback Request permissions
          </label>
        </div>
        <div className="permissions">
          <input
            onClick={handleClickViewFeedbackRequest}
            id="view-feedback-request-permissions"
            type="checkbox"
            value={viewFeedbackRequestPermissions}
          />
          <label htmlFor="view-feedback-request-permissions">
            View Feedback Request permissions
          </label>
        </div>
      </div>

      <div className="permissions-list">
        <h2>Edit Feedback Answer Permissions Below:</h2>
        <div className="permissions">
          <input
            onClick={handleClickViewFeedbackAnswer}
            id="view-feedback-answer-permissions"
            type="checkbox"
            value={viewFeedbackAnswerPermissions}
          />
          <label htmlFor="view-feedback-answer-permissions">
            View Feedback Answer permissions
          </label>
        </div>
      </div>

      <div className="permissions-list">
        <h2>Edit Organization Members Permissions Below:</h2>
        <div className="permissions">
          <input
            onClick={handleClickUpdateOrgMembersPermissions}
            id="update-permissions"
            type="checkbox"
            value={updateOrgMembersPermissions}
          />
          <label htmlFor="update-permissions">
            Update Organization Members permissions
          </label>
        </div>
        <div className="permissions">
          <input
            onClick={handleClickDeleteOrgMembers}
            id="delete-permissions"
            type="checkbox"
            value={deleteOrgMembersPermissions}
          />
          <label htmlFor="delete-permissions">
            Delete Organization Members permissions
          </label>
        </div>
      </div>

      <div className="permissions-list">
        <h2>Edit Role Permissions Below:</h2>
        <div className="permissions">
          <input
            onClick={handleClickRolePermissionsView}
            id="view-role-permissions"
            type="checkbox"
            value={viewRolePermissions}
          />
          <label htmlFor="view-role-permissions">View Role permissions</label>
        </div>
        <div className="permissions">
          <input
            onClick={handleClickRolePermissionsAssign}
            id="assign-role-permissions"
            type="checkbox"
            value={assignRolePermissions}
          />
          <label htmlFor="assign-role-permissions">
            Assign Role permissions
          </label>
        </div>
      </div>

      <div className="permissions-list">
        <h2>Edit View Permissions Below:</h2>
        <div className="permissions">
          <input
            onClick={handleClickView}
            id="view-permissions"
            type="checkbox"
            value={viewPermissions}
          />
          <label htmlFor="view-permissions">View permissions</label>
        </div>
      </div>

      <div className="permissions-list">
        <h2>Edit View Reports Permissions Below:</h2>
        <div className="permissions">
          <input
            onClick={handleClickViewSkillsReports}
            id="view-skills-reports"
            type="checkbox"
            value={viewSkillsReports}
          />
          <label htmlFor="view-skills-reports">View Skills Reports</label>
        </div>
        <div className="permissions">
          <input
            onClick={handleClickViewRetentionReports}
            id="view-retention-reports"
            type="checkbox"
            value={viewRetentionReports}
          />
          <label htmlFor="view-retention-reports">View Retention Reports</label>
        </div>
        <div className="permissions">
          <input
            onClick={handleClickViewAnniversaryReports}
            id="view-anniversary-reports"
            type="checkbox"
            value={viewAnniversaryReports}
          />
          <label htmlFor="view-anniversary-reports">
            View Anniversary Reports
          </label>
        </div>
        <div className="permissions">
          <input
            onClick={handleClickViewBirthdayReports}
            id="view-birthday-reports"
            type="checkbox"
            value={viewBirthdayReports}
          />
          <label htmlFor="view-birthday-reports">View Birthday Reports</label>
        </div>
        <div className="permissions">
          <input
            onClick={handleClickViewProfileReports}
            id="view-profile-reports"
            type="checkbox"
            value={viewProfileReports}
          />
          <label htmlFor="view-profile-reports">View Profile Reports</label>
        </div>
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
