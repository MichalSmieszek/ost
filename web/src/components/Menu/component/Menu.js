import React, { Component } from 'react'
import PropTypes from 'prop-types'
import AppBar from 'material-ui/AppBar'
import { List } from 'material-ui/List'
import UserComponent from '../../Bar/User'
import './Menu.scss'

class Menu extends Component {
  constructor () {
    super()
    this.state = {
      version: ''
    }
  }

  static propTypes = {
    role: PropTypes.string,
    isLoggedIn: PropTypes.bool
  }
  componentWillMount () {
    fetch('/version.txt')
    .then(response => response.text())
      .then(data => this.setState({ version: data }))
  }

  render () {
    return (
      <div>
        <AppBar
          zDepth={3}
          style={{ backgroundColor: 'white', height: 74 }}
          iconElementLeft={
            <div style={{ display: 'flex' }} className='menu__info'>
              <a href='/' style={{ display: 'flex', alignItems: 'center' }}>
                <img className='img-responsive pull-left logo' src='/images/driver-mini-logo.png' />
                <span className='driver-title'>
                  Observer Support Tool
                </span>
              </a>
              <p className='version__text'>v.{this.state.version}</p>
            </div>
          }
          iconElementRight={
            this.props.isLoggedIn &&
            <List style={{ display: 'flex', height: '100%', alignItems: 'center' }}>
              <UserComponent role={this.props.role} activeClassName='route--active' />
            </List>
          }
  />
      </div>
    )
  }
}

export default Menu
