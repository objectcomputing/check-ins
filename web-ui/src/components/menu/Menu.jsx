import { Link } from "react-router-dom";
import React, { useContext, useState } from "react";
import { AppContext, UPDATE_SELECTED_PROFILE } from "../../context/AppContext";
import AppBar from "@material-ui/core/AppBar";
import CssBaseline from "@material-ui/core/CssBaseline";
import Drawer from "@material-ui/core/Drawer";
import Hidden from "@material-ui/core/Hidden";
import IconButton from "@material-ui/core/IconButton";
import MenuIcon from "@material-ui/icons/Menu";
import Toolbar from "@material-ui/core/Toolbar";
import Button from "@material-ui/core/Button";
import { makeStyles, useTheme } from "@material-ui/core/styles";
import AvatarComponent from "../avatar/Avatar";
import MenuItem from "@material-ui/core/MenuItem";
import MenuList from "@material-ui/core/MenuList";
import Paper from "@material-ui/core/Paper";
import Popper from "@material-ui/core/Popper";
import ClickAwayListener from "@material-ui/core/ClickAwayListener";
import Grow from "@material-ui/core/Grow";

import "./Menu.css";

const drawerWidth = 150;

const useStyles = makeStyles((theme) => ({
  root: {
    display: "flex",
  },
  drawer: {
    [theme.breakpoints.up("sm")]: {
      width: drawerWidth,
      flexShrink: 0,
    },
  },
  appBar: {
    backgroundColor: "#e4e3e4",
    [theme.breakpoints.up("sm")]: {
      width: `calc(100% - ${drawerWidth}px)`,
      marginLeft: drawerWidth,
    },
  },
  menuButton: {
    marginRight: theme.spacing(2),
    [theme.breakpoints.up("sm")]: {
      display: "none",
    },
  },
  // necessary for content to be below app bar
  // toolbar: theme.mixins.toolbar,
  drawerPaper: {
    width: drawerWidth,
    backgroundColor: "#a5a4a8",
  },
  content: {
    flexGrow: 1,
    padding: theme.spacing(3),
  },
}));

function Menu() {
  const { dispatch } = useContext(AppContext);
  const { state } = useContext(AppContext);
  const { userProfile } = state;
  const { imageUrl } = userProfile
      ? userProfile
      : {};
  const classes = useStyles();
  const theme = useTheme();
  const [mobileOpen, setMobileOpen] = useState(false);
  const [open, setOpen] = useState(false);
  const anchorRef = React.useRef(null);

  const handleToggle = () => {
    setOpen((prevOpen) => !prevOpen);
  };

  const handleClose = (event) => {
    if (anchorRef.current && anchorRef.current.contains(event.target)) {
      return;
    }

    setOpen(false);
  };

  // return focus to the button when we transitioned from !open -> open
  const prevOpen = React.useRef(open);
  React.useEffect(() => {
    if (prevOpen.current === true && open === false) {
      anchorRef.current.focus();
    }

    prevOpen.current = open;
  }, [open]);

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const linkStyle = { textDecoration: "none", color: "white" };

  const drawer = (
    <div>
      <div className={classes.toolbar} />
      <div style={{ display: "flex", justifyContent: "center" }}>
        <img
          alt="Object Computing, Inc."
          src="/img/ocicube-white.png"
          style={{ width: "50%" }}
        />
      </div>
      <br />
      <Button size="large" style={{ width: "100%" }}>
        <Link style={linkStyle} to="/">
          Home
        </Link>
      </Button>
      <br />
      <Button size="large" style={{ width: "100%" }}>
        <Link style={linkStyle} to="/teams">
          Teams
        </Link>
      </Button>
      <br />
      <Button
        onClick={() =>
          dispatch({
            type: UPDATE_SELECTED_PROFILE,
            payload: undefined,
          })
        }
        size="large"
        style={{ width: "100%" }}
      >
        <Link style={linkStyle} to="/checkins">
          Check-ins
        </Link>
      </Button>
      <br />
      <Button size="large" style={{ width: "100%" }}>
        <Link style={linkStyle} to="/directory">
          Directory
        </Link>
      </Button>
      <br />
      {/* {isAdmin && (
        <Button>
          <Link style={linkStyle} to="/admin">
            Edit PDLs
          </Link>
        </Button>
      )} */}
    </div>
  );

  return (
    <div className={classes.root} style={{ paddingRight: `${drawerWidth}px` }}>
      <CssBaseline />
      <AppBar position="fixed" className={classes.appBar}>
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={handleDrawerToggle}
            className={classes.menuButton}
          >
            <MenuIcon />
          </IconButton>
        </Toolbar>
        <div
          ref={anchorRef}
          aria-controls={open ? "menu-list-grow" : undefined}
          aria-haspopup="true"
          onClick={handleToggle}
        >
          <AvatarComponent imageUrl={imageUrl} style={{
              position: "absolute",
              right: "5px",
              top: "10px",
          }} />
        </div>
        <Popper
          open={open}
          anchorEl={anchorRef.current}
          transition
          disablePortal
          style={{ left: "unset", margin: "0 auto", right: 0, width: "100px" }}
        >
          {({ TransitionProps, placement }) => (
            <Grow
              {...TransitionProps}
              style={{
                transformOrigin:
                  placement === "bottom" ? "center top" : "center bottom",
              }}
            >
              <Paper>
                <ClickAwayListener onClickAway={handleClose}>
                  <MenuList autoFocusItem={open} id="menu-list-grow">
                    <MenuItem onClick={handleToggle}>
                      <Link style={{ textDecoration: "none" }} to="/profile">
                        Profile
                      </Link>
                    </MenuItem>
                    <MenuItem onClick={() => window.location.reload()}>
                      <Link style={{ textDecoration: "none" }} to={`/logout`}>
                        Logout
                      </Link>
                    </MenuItem>
                  </MenuList>
                </ClickAwayListener>
              </Paper>
            </Grow>
          )}
        </Popper>
      </AppBar>
      <nav className={classes.drawer} aria-label="mailbox folders">
        <Hidden smUp implementation="css">
          <Drawer
            variant="temporary"
            anchor={theme.direction === "rtl" ? "right" : "left"}
            open={mobileOpen}
            onClose={handleDrawerToggle}
            classes={{
              paper: classes.drawerPaper,
            }}
            ModalProps={{
              keepMounted: true, // Better open performance on mobile.
            }}
          >
            {drawer}
          </Drawer>
        </Hidden>
        <Hidden xsDown implementation="css">
          <Drawer
            classes={{
              paper: classes.drawerPaper,
            }}
            variant="permanent"
            open
          >
            {drawer}
          </Drawer>
        </Hidden>
      </nav>
      <main className={classes.content}>
        <div className={classes.toolbar} />
      </main>
    </div>
  );
}

export default Menu;
