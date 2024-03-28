import React from "react";
import {
  Avatar,
  Card,
  CardHeader,
  IconButton,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  Tooltip, Typography
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import RemoveIcon from "@mui/icons-material/Remove";
import {getAvatarURL} from "../../api/api";

const MemberSelector = () => {

  return (
    <Card>
      <CardHeader
        title="Selected Members"
        action={
          <Tooltip title="Add members" arrow>
            <IconButton><AddIcon/></IconButton>
          </Tooltip>
        }
      />
      <List dense role="list">
        <ListItem
          role="listitem"
          secondaryAction={
            <Tooltip title="Deselect member" arrow>
              <IconButton><RemoveIcon/></IconButton>
            </Tooltip>
          }
        >
          <ListItemAvatar>
            <Avatar src={getAvatarURL("kimberlinm@objectcomputing.com")}/>
          </ListItemAvatar>
          <ListItemText
            primary={<Typography fontWeight="bold">{"Michael Kimberlin"}</Typography>}
            secondary={<Typography color="textSecondary" component="h6">{"Director of Organizational Development"}</Typography>}
          />
        </ListItem>
      </List>
    </Card>
  );

};

export default MemberSelector;