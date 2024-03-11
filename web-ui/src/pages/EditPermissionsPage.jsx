import React, { useEffect, useContext, useState } from "react";

import EditPermissionsPageRoles from "./EditPermissionsPageRoles";

import { getPermissionsList } from "../api/permissions";
import {
  getRolePermissionsList,
  postRolePermissionsList,
  deleteRolePermissionsList,
} from "../api/rolepermissions";
import { getMemberRolesList } from "../api/memberroles";
import { isArrayPresent, filterObjectByValOrKey } from "../helpers/checks";

import { UPDATE_TOAST } from "../context/actions";

import { AppContext } from "../context/AppContext";
// import { selectPermissions } from "../context/selectors";
import { selectCurrentUserId } from "../context/selectors";

import "./EditPermissionsPage.css";

const EditPermissionsPage = (props) => {
  const { state } = useContext(AppContext);
  const { csrf } = state;
  const [permissionsList, setPermissionsList] = useState([]);
  const [adminId, setAdminId] = useState("");
  const [pdlId, setPDLId] = useState("");
  const [memberId, setMemberId] = useState("");
  const [rolePermissionsList, setRolePermissionsList] = useState([]);
  const currentUserId = selectCurrentUserId(state);
  const [currentUserRole, setCurrentUserRole] = useState("");
  const [memberRoles, setMemberRoles] = useState([]);

  const [isAdminRole, setIsAdminRole] = useState(false);

  const [adminPermissionsList, setAdminPermissionsList] = useState([]);
  const [pdlPermissionsList, setPDLPermissionsList] = useState([]);
  const [memberPermissionsList, setMemberPermissionsList] = useState([]);

  const [
    createFeedbackRequestPermissionsId,
    setCreateFeedbackRequestPermissionsId,
  ] = useState("");
  const [
    deleteFeedbackRequestPermissionsId,
    setDeleteFeedbackRequestPermissionsId,
  ] = useState("");
  const [
    viewFeedbackRequestPermissionsId,
    setViewFeedbackRequestPermissionsId,
  ] = useState("");
  const [viewFeedbackAnswerPermissionsId, setViewFeedbackAnswerPermissionsId] =
    useState("");
  const [createOrgMembersPermissionsId, setCreateOrgMembersPermissionsId] =
    useState("");
  const [deleteOrgMembersPermissionsId, setDeleteOrgMembersPermissionsId] =
    useState("");
  const [viewRolePermissionsId, setViewRolePermissionsId] = useState("");
  const [assignRolePermissionsId, setAssignRolePermissionsId] = useState("");
  const [viewPermissionsId, setViewPermissionsId] = useState("");
  const [viewSkillsReportsId, setViewSkillsReportsId] = useState("");
  const [viewRetentionReportsId, setViewRetentionReportsId] = useState("");
  const [viewAnniversaryReportsId, setViewAnniversaryReportsId] = useState("");
  const [viewBirthdayReportsId, setViewBirthdayReportsId] = useState("");
  const [viewProfileReportsId, setViewProfileReportsId] = useState("");
  const [updateCheckinsId, setUpdateCheckinsId] = useState("");
  const [createCheckinsId, setCreateCheckinsId] = useState("");
  const [viewCheckinsId, setViewCheckinsId] = useState("");

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

  const [updateCheckinsAdmin, setUpdateCheckinsAdmin] = useState(false);
  const [updateCheckinsPDL, setUpdateCheckinsPDL] = useState(false);
  const [updateCheckinsMember, setUpdateCheckinsMember] = useState(false);

  const [createCheckinsAdmin, setCreateCheckinsAdmin] = useState(false);
  const [createCheckinsPDL, setCreateCheckinsPDL] = useState(false);
  const [createCheckinsMember, setCreateCheckinsMember] = useState(false);

  const [viewCheckinsAdmin, setViewCheckinsAdmin] = useState(false);
  const [viewCheckinsPDL, setViewCheckinsPDL] = useState(false);
  const [viewCheckinsMember, setViewCheckinsMember] = useState(false);

  const changeRolePermission = async (roleId, permissionId) => {
    let newSchema = { roleId: roleId, permissionId: permissionId };
    let res = await postRolePermissionsList(newSchema, csrf);
    let data =
      res.payload && res.payload.data && !res.error ? res.payload.data : null;
    if (data) {
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "success",
          toast: `Permission added to Role`,
        },
      });
    } else {
      console.log(res?.error);
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "warning",
          toast: `Problem changing permission for that role`,
        },
      });
    }
  };

  const deleteRolePermission = async (roleId, permissionId) => {
    let newSchema = { roleId: roleId, permissionId: permissionId };
    let res = await deleteRolePermissionsList(newSchema, csrf);
    let data = !res.error ? "Success" : null;
    if (data) {
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "success",
          toast: `Permission removed from Role`,
        },
      });
    } else {
      console.log(res?.error);
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "warning",
          toast: `Problem deleting permission for that role`,
        },
      });
    }
  };

  const handleClickCreateFeedbackRequestAdmin = () => {
    if (!createFeedbackRequestPermissionsAdmin) {
      changeRolePermission(adminId, createFeedbackRequestPermissionsId);
    } else {
      deleteRolePermission(adminId, createFeedbackRequestPermissionsId);
    }
    setCreateFeedbackRequestPermissionsAdmin(
      !createFeedbackRequestPermissionsAdmin
    );
  };
  const handleClickCreateFeedbackRequestPDL = () => {
    if (!createFeedbackRequestPermissionsPDL) {
      changeRolePermission(pdlId, createFeedbackRequestPermissionsId);
    } else {
      deleteRolePermission(pdlId, createFeedbackRequestPermissionsId);
    }
    setCreateFeedbackRequestPermissionsPDL(
      !createFeedbackRequestPermissionsPDL
    );
  };

  const handleClickCreateFeedbackRequestMember = () => {
    if (!createFeedbackRequestPermissionsMember) {
      changeRolePermission(memberId, createFeedbackRequestPermissionsId);
    } else {
      deleteRolePermission(memberId, createFeedbackRequestPermissionsId);
    }
    setCreateFeedbackRequestPermissionsMember(
      !createFeedbackRequestPermissionsMember
    );
  };

  const handleClickDeleteFeedbackRequestAdmin = () => {
    if (!deleteFeedbackRequestPermissionsAdmin) {
      changeRolePermission(adminId, deleteFeedbackRequestPermissionsId);
    } else {
      deleteRolePermission(adminId, deleteFeedbackRequestPermissionsId);
    }
    setDeleteFeedbackRequestPermissionsAdmin(
      !deleteFeedbackRequestPermissionsAdmin
    );
  };
  const handleClickDeleteFeedbackRequestPDL = () => {
    if (!deleteFeedbackRequestPermissionsPDL) {
      changeRolePermission(pdlId, deleteFeedbackRequestPermissionsId);
    } else {
      deleteRolePermission(pdlId, deleteFeedbackRequestPermissionsId);
    }
    setDeleteFeedbackRequestPermissionsPDL(
      !deleteFeedbackRequestPermissionsPDL
    );
  };
  const handleClickDeleteFeedbackRequestMember = () => {
    if (!deleteFeedbackRequestPermissionsMember) {
      changeRolePermission(memberId, deleteFeedbackRequestPermissionsId);
    } else {
      deleteRolePermission(memberId, deleteFeedbackRequestPermissionsId);
    }
    setDeleteFeedbackRequestPermissionsMember(
      !deleteFeedbackRequestPermissionsMember
    );
  };

  const handleClickViewFeedbackRequestAdmin = () => {
    if (!viewFeedbackRequestPermissionsAdmin) {
      changeRolePermission(adminId, viewFeedbackRequestPermissionsId);
    } else {
      deleteRolePermission(adminId, viewFeedbackRequestPermissionsId);
    }
    setViewFeedbackRequestPermissionsAdmin(
      !viewFeedbackRequestPermissionsAdmin
    );
  };
  const handleClickViewFeedbackRequestPDL = () => {
    if (!viewFeedbackRequestPermissionsPDL) {
      changeRolePermission(pdlId, viewFeedbackRequestPermissionsId);
    } else {
      deleteRolePermission(pdlId, viewFeedbackRequestPermissionsId);
    }
    setViewFeedbackRequestPermissionsPDL(!viewFeedbackRequestPermissionsPDL);
  };

  const handleClickViewFeedbackRequestMember = () => {
    if (!viewFeedbackRequestPermissionsMember) {
      changeRolePermission(memberId, viewFeedbackRequestPermissionsId);
    } else {
      deleteRolePermission(memberId, viewFeedbackRequestPermissionsId);
    }
    setViewFeedbackRequestPermissionsMember(
      !viewFeedbackRequestPermissionsMember
    );
  };

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

  const handleClickUpdateCheckinsAdmin = () =>
    setUpdateCheckinsAdmin(!updateCheckinsAdmin);
  const handleClickUpdateCheckinsPDL = () =>
    setUpdateCheckinsPDL(!updateCheckinsPDL);
  const handleClickUpdateCheckinsMember = () =>
    setUpdateCheckinsMember(!updateCheckinsMember);

  const handleClickCreateCheckinsAdmin = () =>
    setCreateCheckinsAdmin(!createCheckinsAdmin);
  const handleClickCreateCheckinsPDL = () =>
    setCreateCheckinsPDL(!createCheckinsPDL);
  const handleClickCreateCheckinsMember = () =>
    setCreateCheckinsMember(!createCheckinsMember);

  const handleClickViewCheckinsAdmin = () =>
    setViewCheckinsAdmin(!viewCheckinsAdmin);
  const handleClickViewCheckinsPDL = () => setViewCheckinsPDL(!viewCheckinsPDL);
  const handleClickViewCheckinsMember = () =>
    setViewCheckinsMember(!viewCheckinsMember);

  useEffect(() => {
    const doTask1 = async () => {
      let res = await getRolePermissionsList(csrf);
      let data =
        res.payload && res.payload.data && !res.error ? res.payload.data : null;
      if (data) {
        setRolePermissionsList(data);
      }
    };
    const doTask2 = async () => {
      let res = await getPermissionsList(csrf);
      let data =
        res.payload && res.payload.data && !res.error ? res.payload.data : null;
      if (data) {
        setPermissionsList(data);
      }
    };
    const doTask3 = async () => {
      let res = await getMemberRolesList(csrf);
      let data =
        res.payload && res.payload.data && !res.error ? res.payload.data : null;
      if (data) {
        setMemberRoles(data);
      }
    };

    if (csrf) {
      doTask1();
      doTask2();
      doTask3();
    }
  }, [csrf]);

  useEffect(() => {
    if (isArrayPresent(rolePermissionsList)) {
      let adminData = rolePermissionsList.filter((a) => a.role === "ADMIN");
      if (isArrayPresent(adminData)) {
        setAdminId(adminData[0].roleId);
      }
      let pdlData = rolePermissionsList.filter((a) => a.role === "PDL");
      if (isArrayPresent(pdlData)) {
        setPDLId(pdlData[0].roleId);
      }
      let memberData = rolePermissionsList.filter((a) => a.role === "PDL");
      if (isArrayPresent(memberData)) {
        setMemberId(memberData[0].roleId);
      }
    }

    if (isArrayPresent(permissionsList)) {
      let id1 = permissionsList.filter(
        (a) => a.permission === "CAN_CREATE_ORGANIZATION_MEMBERS"
      );
      if (isArrayPresent(id1)) {
        setCreateFeedbackRequestPermissionsId(id1[0].id);
      }
      let id2 = permissionsList.filter(
        (a) => a.permission === "CAN_DELETE_ORGANIZATION_MEMBERS"
      );
      if (isArrayPresent(id2)) {
        setCreateFeedbackRequestPermissionsId(id2[0].id);
      }
      let id3 = permissionsList.filter(
        (a) => a.permission === "CAN_DELETE_FEEDBACK_REQUEST"
      );
      if (isArrayPresent(id3)) {
        setDeleteFeedbackRequestPermissionsId(id3[0].id);
      }
      let id4 = permissionsList.filter(
        (a) => a.permission === "CAN_VIEW_FEEDBACK_REQUEST"
      );
      if (isArrayPresent(id4)) {
        setViewFeedbackRequestPermissionsId(id4[0].id);
      }
      let id5 = permissionsList.filter(
        (a) => a.permission === "CAN_VIEW_FEEDBACK_ANSWER"
      );
      if (isArrayPresent(id5)) {
        setViewFeedbackAnswerPermissionsId(id5[0].id);
      }
      let id6 = permissionsList.filter(
        (a) => a.permission === "CAN_CREATE_ORGANIZATION_MEMBERS"
      );
      if (isArrayPresent(id6)) {
        setCreateOrgMembersPermissionsId(id6[0].id);
      }
      let id7 = permissionsList.filter(
        (a) => a.permission === "CAN_DELETE_ORGANIZATION_MEMBERS"
      );
      if (isArrayPresent(id7)) {
        setDeleteOrgMembersPermissionsId(id7[0].id);
      }
      let id8 = permissionsList.filter(
        (a) => a.permission === "CAN_VIEW_ROLE_PERMISSIONS"
      );
      if (isArrayPresent(id8)) {
        setViewRolePermissionsId(id8[0].id);
      }
      let id9 = permissionsList.filter(
        (a) => a.permission === "CAN_ASSIGN_ROLE_PERMISSIONS"
      );
      if (isArrayPresent(id9)) {
        setAssignRolePermissionsId(id9[0].id);
      }
      let id10 = permissionsList.filter(
        (a) => a.permission === "CAN_VIEW_PERMISSIONS"
      );
      if (isArrayPresent(id10)) {
        setViewPermissionsId(id10[0].id);
      }
      let id11 = permissionsList.filter(
        (a) => a.permission === "CAN_VIEW_SKILLS_REPORT"
      );
      if (isArrayPresent(id11)) {
        setViewSkillsReportsId(id11[0].id);
      }
      let id12 = permissionsList.filter(
        (a) => a.permission === "CAN_VIEW_RETENTION_REPORT"
      );
      if (isArrayPresent(id12)) {
        setViewRetentionReportsId(id12[0].id);
      }
      let id13 = permissionsList.filter(
        (a) => a.permission === "CAN_VIEW_ANNIVERSARY_REPORT"
      );
      if (isArrayPresent(id13)) {
        setViewAnniversaryReportsId(id13[0].id);
      }
      let id14 = permissionsList.filter(
        (a) => a.permission === "CAN_VIEW_BIRTHDAY_REPORT"
      );
      if (isArrayPresent(id14)) {
        setViewBirthdayReportsId(id14[0].id);
      }
      let id15 = permissionsList.filter(
        (a) => a.permission === "CAN_VIEW_PROFILE_REPORT"
      );
      if (isArrayPresent(id15)) {
        setViewProfileReportsId(id15[0].id);
      }
      let id16 = permissionsList.filter(
        (a) => a.permission === "CAN_UPDATE_CHECKINS"
      );
      if (isArrayPresent(id16)) {
        setUpdateCheckinsId(id16[0].id);
      }
      let id17 = permissionsList.filter(
        (a) => a.permission === "CAN_CREATE_CHECKINS"
      );
      if (isArrayPresent(id17)) {
        setCreateCheckinsId(id17[0].id);
      }
      let id18 = permissionsList.filter(
        (a) => a.permission === "CAN_VIEW_CHECKINS"
      );
      if (isArrayPresent(id18)) {
        setViewCheckinsId(id18[0].id);
      }
    }
  }, [rolePermissionsList, permissionsList]);

  useEffect(() => {
    if (isArrayPresent(memberRoles)) {
      let data = memberRoles.filter(
        (a) => a.memberRoleId.memberId === currentUserId
      );
      if (isArrayPresent(data)) {
        let role = filterObjectByValOrKey(
          rolePermissionsList,
          data[0].memberRoleId.roleId
        );
        if (isArrayPresent(role)) {
          console.log("This is role", role);
          setCurrentUserRole(role[0].role);
        }
      }
    }

    if (currentUserRole === "ADMIN") {
      setIsAdminRole(true);
    } else {
      setIsAdminRole(false);
    }

    console.log("Member Roles");
    console.log(memberRoles);
    console.log("Current User Role:");
    console.log(currentUserRole);
  }, [memberRoles, currentUserRole, csrf, rolePermissionsList, currentUserId]);

  useEffect(() => {
    let adminRole = filterObjectByValOrKey(
      rolePermissionsList,
      "ADMIN",
      "role"
    );
    if (isArrayPresent(adminRole)) {
      setAdminPermissionsList(adminRole[0].permissions);
      console.log("This is the admin permissions list", adminPermissionsList);
    }
  }, [rolePermissionsList, adminPermissionsList]);

  useEffect(() => {
    let pdlRole = filterObjectByValOrKey(rolePermissionsList, "PDL", "role");
    if (isArrayPresent(pdlRole)) {
      setPDLPermissionsList(pdlRole[0].permissions);
      console.log("This is the PDL permissions list", pdlPermissionsList);
    }
  }, [rolePermissionsList, pdlPermissionsList]);

  useEffect(() => {
    let memberRole = filterObjectByValOrKey(
      rolePermissionsList,
      "MEMBER",
      "role"
    );
    if (isArrayPresent(memberRole)) {
      setMemberPermissionsList(memberRole[0].permissions);
      console.log("This is the Member permissions list", memberPermissionsList);
    }
  }, [rolePermissionsList, memberPermissionsList]);

  useEffect(() => {
    setCreateFeedbackRequestPermissionsAdmin(
      adminPermissionsList.some(
        (permission) => permission.permission === "CAN_CREATE_FEEDBACK_REQUEST"
      )
    );
    setDeleteFeedbackRequestPermissionsAdmin(
      adminPermissionsList.some(
        (permission) => permission.permission === "CAN_DELETE_FEEDBACK_REQUEST"
      )
    );
    setViewFeedbackRequestPermissionsAdmin(
      adminPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_FEEDBACK_REQUEST"
      )
    );
    setViewFeedbackAnswerPermissionsAdmin(
      adminPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_FEEDBACK_ANSWER"
      )
    );
    setCreateOrgMembersPermissionsAdmin(
      adminPermissionsList.some(
        (permission) =>
          permission.permission === "CAN_CREATE_ORGANIZATION_MEMBERS"
      )
    );
    setDeleteOrgMembersPermissionsAdmin(
      adminPermissionsList.some(
        (permission) =>
          permission.permission === "CAN_DELETE_ORGANIZATION_MEMBERS"
      )
    );
    setViewRolePermissionsAdmin(
      adminPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_ROLE_PERMISSIONS"
      )
    );
    setAssignRolePermissionsAdmin(
      adminPermissionsList.some(
        (permission) => permission.permission === "CAN_ASSIGN_ROLE_PERMISSIONS"
      )
    );
    setViewPermissionsAdmin(
      adminPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_PERMISSIONS"
      )
    );
    setViewSkillsReportsAdmin(
      adminPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_SKILLS_REPORT"
      )
    );
    setViewRetentionReportsAdmin(
      adminPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_RETENTION_REPORT"
      )
    );
    setViewAnniversaryReportsAdmin(
      adminPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_ANNIVERSARY_REPORT"
      )
    );
    setViewBirthdayReportsAdmin(
      adminPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_BIRTHDAY_REPORT"
      )
    );
    setViewProfileReportsAdmin(
      adminPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_PROFILE_REPORT"
      )
    );
    setUpdateCheckinsAdmin(
      adminPermissionsList.some(
        (permission) => permission.permission === "CAN_UPDATE_CHECKINS"
      )
    );
    setCreateCheckinsAdmin(
      adminPermissionsList.some(
        (permission) => permission.permission === "CAN_CREATE_CHECKINS"
      )
    );
    setViewCheckinsAdmin(
      adminPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_CHECKINS"
      )
    );
  }, [adminPermissionsList]);

  useEffect(() => {
    setCreateFeedbackRequestPermissionsPDL(
      pdlPermissionsList.some(
        (permission) => permission.permission === "CAN_CREATE_FEEDBACK_REQUEST"
      )
    );
    setDeleteFeedbackRequestPermissionsPDL(
      pdlPermissionsList.some(
        (permission) => permission.permission === "CAN_DELETE_FEEDBACK_REQUEST"
      )
    );
    setViewFeedbackRequestPermissionsPDL(
      pdlPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_FEEDBACK_REQUEST"
      )
    );
    setViewFeedbackAnswerPermissionsPDL(
      pdlPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_FEEDBACK_ANSWER"
      )
    );
    setCreateOrgMembersPermissionsPDL(
      pdlPermissionsList.some(
        (permission) =>
          permission.permission === "CAN_CREATE_ORGANIZATION_MEMBERS"
      )
    );
    setDeleteOrgMembersPermissionsPDL(
      pdlPermissionsList.some(
        (permission) =>
          permission.permission === "CAN_DELETE_ORGANIZATION_MEMBERS"
      )
    );
    setViewRolePermissionsPDL(
      pdlPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_ROLE_PERMISSIONS"
      )
    );
    setAssignRolePermissionsPDL(
      pdlPermissionsList.some(
        (permission) => permission.permission === "CAN_ASSIGN_ROLE_PERMISSIONS"
      )
    );
    setViewPermissionsPDL(
      pdlPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_PERMISSIONS"
      )
    );
    setViewSkillsReportsPDL(
      pdlPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_SKILLS_REPORT"
      )
    );
    setViewRetentionReportsPDL(
      pdlPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_RETENTION_REPORT"
      )
    );
    setViewAnniversaryReportsPDL(
      pdlPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_ANNIVERSARY_REPORT"
      )
    );
    setViewBirthdayReportsPDL(
      pdlPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_BIRTHDAY_REPORT"
      )
    );
    setViewProfileReportsPDL(
      pdlPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_PROFILE_REPORT"
      )
    );
    setUpdateCheckinsPDL(
      pdlPermissionsList.some(
        (permission) => permission.permission === "CAN_UPDATE_CHECKINS"
      )
    );
    setCreateCheckinsPDL(
      pdlPermissionsList.some(
        (permission) => permission.permission === "CAN_CREATE_CHECKINS"
      )
    );
    setViewCheckinsPDL(
      pdlPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_CHECKINS"
      )
    );
  }, [pdlPermissionsList]);

  useEffect(() => {
    setCreateFeedbackRequestPermissionsMember(
      memberPermissionsList.some(
        (permission) => permission.permission === "CAN_CREATE_FEEDBACK_REQUEST"
      )
    );
    setDeleteFeedbackRequestPermissionsMember(
      memberPermissionsList.some(
        (permission) => permission.permission === "CAN_DELETE_FEEDBACK_REQUEST"
      )
    );
    setViewFeedbackRequestPermissionsMember(
      memberPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_FEEDBACK_REQUEST"
      )
    );
    setViewFeedbackAnswerPermissionsMember(
      memberPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_FEEDBACK_ANSWER"
      )
    );
    setCreateOrgMembersPermissionsMember(
      memberPermissionsList.some(
        (permission) =>
          permission.permission === "CAN_CREATE_ORGANIZATION_MEMBERS"
      )
    );
    setDeleteOrgMembersPermissionsMember(
      memberPermissionsList.some(
        (permission) =>
          permission.permission === "CAN_DELETE_ORGANIZATION_MEMBERS"
      )
    );
    setViewRolePermissionsMember(
      memberPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_ROLE_PERMISSIONS"
      )
    );
    setAssignRolePermissionsMember(
      memberPermissionsList.some(
        (permission) => permission.permission === "CAN_ASSIGN_ROLE_PERMISSIONS"
      )
    );
    setViewPermissionsMember(
      memberPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_PERMISSIONS"
      )
    );
    setViewSkillsReportsMember(
      memberPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_SKILLS_REPORT"
      )
    );
    setViewRetentionReportsMember(
      memberPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_RETENTION_REPORT"
      )
    );
    setViewAnniversaryReportsMember(
      memberPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_ANNIVERSARY_REPORT"
      )
    );
    setViewBirthdayReportsMember(
      memberPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_BIRTHDAY_REPORT"
      )
    );
    setViewProfileReportsMember(
      memberPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_PROFILE_REPORT"
      )
    );
    setUpdateCheckinsMember(
      memberPermissionsList.some(
        (permission) => permission.permission === "CAN_UPDATE_CHECKINS"
      )
    );
    setCreateCheckinsMember(
      memberPermissionsList.some(
        (permission) => permission.permission === "CAN_CREATE_CHECKINS"
      )
    );
    setViewCheckinsMember(
      memberPermissionsList.some(
        (permission) => permission.permission === "CAN_VIEW_CHECKINS"
      )
    );
  }, [memberPermissionsList]);

  return (
    <div className="edit-permissions-page">
      {isAdminRole ? (
        <>
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

            <EditPermissionsPageRoles
              title="Update Checkins"
              selectAdmin={handleClickUpdateCheckinsAdmin}
              admin={updateCheckinsAdmin}
              selectPDL={handleClickUpdateCheckinsPDL}
              pdl={updateCheckinsPDL}
              selectMember={handleClickUpdateCheckinsMember}
              member={updateCheckinsMember}
            />

            <EditPermissionsPageRoles
              title="Create Checkins"
              selectAdmin={handleClickCreateCheckinsAdmin}
              admin={createCheckinsAdmin}
              selectPDL={handleClickCreateCheckinsPDL}
              pdl={createCheckinsPDL}
              selectMember={handleClickCreateCheckinsMember}
              member={createCheckinsMember}
            />

            <EditPermissionsPageRoles
              title="View Checkins"
              selectAdmin={handleClickViewCheckinsAdmin}
              admin={viewCheckinsAdmin}
              selectPDL={handleClickViewCheckinsPDL}
              pdl={viewCheckinsPDL}
              selectMember={handleClickViewCheckinsMember}
              member={viewCheckinsMember}
            />
          </div>
        </>
      ) : (
        <>
          <h3>You do not have permission to view this page.</h3>
        </>
      )}
    </div>
  );
};

export default EditPermissionsPage;
