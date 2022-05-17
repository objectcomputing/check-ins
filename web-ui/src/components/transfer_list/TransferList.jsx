import React, {useState} from "react";
import {
  Avatar,
  Button,
  Card,
  CardHeader,
  Checkbox,
  Divider,
  Grid,
  List,
  ListItem, ListItemAvatar, ListItemButton,
  ListItemText, Typography
} from "@mui/material";
import LeftArrowIcon from "@mui/icons-material/KeyboardArrowLeft";
import RightArrowIcon from "@mui/icons-material/KeyboardArrowRight";
import PropTypes from "prop-types";
import {getAvatarURL} from "../../api/api";

import "./TransferList.css";

const not = (a, b) => a.filter((value) => b.indexOf(value) === -1);
const intersection = (a, b) => a.filter((value) => b.indexOf(value) !== -1);
const union = (a, b) => [...a, ...not(b, a)];

const propTypes = {
  leftList: PropTypes.arrayOf(PropTypes.object).isRequired,
  rightList: PropTypes.arrayOf(PropTypes.object).isRequired,
  leftLabel: PropTypes.string,
  rightLabel: PropTypes.string,
  onListsChanged: PropTypes.func
};

const TransferList = ({ leftList, rightList, leftLabel, rightLabel, onListsChanged }) => {

  const [checked, setChecked] = useState([]);

  const leftChecked = intersection(checked, leftList);
  const rightChecked = intersection(checked, rightList);

  const handleToggle = (value) => {
    const currentIndex = checked.indexOf(value);
    const newChecked = [...checked];

    if (currentIndex === -1) {
      newChecked.push(value);
    } else {
      newChecked.splice(currentIndex, 1);
    }

    setChecked(newChecked);
  }

  const numberOfChecked = (items) => intersection(checked, items).length;

  const handleToggleAll = (items) => {
    if (numberOfChecked(items) === items.length) {
      setChecked(not(checked, items));
    } else {
      setChecked(union(checked, items));
    }
  }

  const handleCheckedRight = () => {
    onListsChanged({
      left: not(leftList, leftChecked),
      right: rightList.concat(leftChecked)
    });
    setChecked(not(checked, leftChecked));
  }

  const handleCheckedLeft = () => {
    onListsChanged({
      left: leftList.concat(rightChecked),
      right: not(rightList, rightChecked)
    });
    setChecked(not(checked, rightChecked));
  }

  const customList = (title, items, emptyMessage) => (
    <Card className="transfer-list" variant="outlined">
      <CardHeader
        action={
          <Checkbox
            onClick={() => handleToggleAll(items)}
            checked={numberOfChecked(items) === items.length && items.length !== 0}
            indeterminate={numberOfChecked(items) !== items.length && numberOfChecked(items) !== 0}
            disabled={items.length === 0}
            style={{ marginRight: "8px", marginTop: "-8px" }}
          />
        }
        title={title}
        subheader={`${numberOfChecked(items)}/${items.length} selected`}
        titleTypographyProps={{
          fontWeight: "bold",
          fontSize: "18px"
        }}
      />
      <Divider/>
      <List
        dense
        role="list"
        sx={{
          width: 600,
          height: 400,
          overflow: "auto",
          bgcolor: "transparent"
        }}
      >
        {items.length === 0 &&
          <div className="empty-list-message-container">
            <Typography className="empty-list-message">{emptyMessage}</Typography>
          </div>
        }
        {items.map((member) => (
          <ListItem
            key={member.id}
            role="listitem"
            onClick={() => handleToggle(member)}
            disablePadding
            secondaryAction={
              <Checkbox
                checked={checked.indexOf(member) !== -1}
                tabIndex={-1}
                disableRipple
              />
            }
          >
            <ListItemButton>
              <ListItemAvatar>
                <Avatar src={getAvatarURL(member?.workEmail)}/>
              </ListItemAvatar>
              <ListItemText
                primary={<Typography fontWeight="bold">{member?.name}</Typography>}
                secondary={<Typography color="textSecondary" component="h6">{member?.title}</Typography>}
              />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
    </Card>
  );

  return (
    <Grid className="transfer-list-container" container spacing={2} justifyContent="center" alignItems="center">
      <Grid item>{customList(leftLabel || "Choices", leftList, "No members to select")}</Grid>
      <Grid item>
        <Grid container direction="column" alignItems="center">
          <Button
            sx={{ my: 1 }}
            variant="outlined"
            size="small"
            onClick={handleCheckedRight}
            disabled={leftChecked.length === 0}
            aria-label="move selected right"
          >
            <RightArrowIcon/>
          </Button>
          <Button
            sx={{ my: 1 }}
            variant="outlined"
            size="small"
            onClick={handleCheckedLeft}
            disabled={rightChecked.length === 0}
            aria-label="move selected left"
          >
            <LeftArrowIcon/>
          </Button>
        </Grid>
      </Grid>
      <Grid item>{customList(rightLabel || "Chosen", rightList, "No recipients")}</Grid>
    </Grid>
  );
}

TransferList.propTypes = propTypes;

export default TransferList;