import React, { Component } from 'react'
import { RaisedButton, Checkbox, TextField, FlatButton } from 'material-ui'
import Spinner from 'react-spinkit'
import PropTypes from 'prop-types'
import 'react-table/react-table.css'
import './Users.scss'
import ReactTable from 'react-table'
import Dialog from 'material-ui/Dialog'
import _ from 'lodash'

// eslint-disable-next-line no-useless-escape
const regex = /[ !@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/
const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/
class Users extends Component {
  state = {
    selectedRow: null,
    usersOptions: [],
    isUserDetailsOpen: false,
    editionMode: '',
    isDoubleClicked: false,
    user: {
      login: '',
      loginError: false,
      password: '',
      passwordError: false,
      passwordConfirmation: '',
      passwordConfirmationError: false,
      firstName: '',
      firstNameError: false,
      lastName: '',
      lastNameError: false,
      email: '',
      emailError: false,
      contact: '',
      contactError: false,
      activated: false,
      rolesIds: null,
      positionId: null
    }
  }

  static propTypes = {
    getAllUsersList: PropTypes.func,
    putUser: PropTypes.func,
    addUser: PropTypes.func,
    putUserPassword: PropTypes.func,
    getSelectedUser: PropTypes.func,
    passwordUpdateFailAction: PropTypes.func,
    allUsersList: PropTypes.object,
    selectedUser: PropTypes.object,
    allUsersListLoading: PropTypes.bool,
    isUserLoading: PropTypes.bool,
    isPasswordUpdated: PropTypes.bool
  }

  componentDidMount () {
    this.props.getAllUsersList()
  }

  componentDidUpdate (prevProps) {
    if (!_.isEqual(prevProps.allUsersList, this.props.allUsersList)) {
      if (this.props.allUsersList.data && this.props.allUsersList.data.length) {
        this.handleUsers(this.props.allUsersList.data)
      }
    } else if (this.props.isPasswordUpdated && !this.props.isUserLoading) {
      this.handleCloseEditUser()
      this.props.passwordUpdateFailAction()
    }
    if (this.state.isDoubleClicked) {
      this.handleEditUser('editUser')
    }
  }

  handleUsers = (users) => {
    let usersOptions = []
    users.map(user => {
      usersOptions.push({
        id: user.id,
        login: user.login,
        firstName: user.firstName,
        lastName: user.lastName,
        deleted: user.activated ? 'No' : 'Yes'
      })
    })
    this.setState({
      usersOptions,
      isUserDetailsOpen: false,
      selectedRow: null
    })
  }

  handleAddUser = (editionMode) => {
    const user = { ...this.state.user }
    user.login = ''
    user.password = ''
    user.passwordConfirmation = ''
    user.firstName = ''
    user.lastName = ''
    user.email = ''
    user.contact = ''
    this.setState({
      user: user,
      isUserDetailsOpen: true,
      editionMode: editionMode
    })
  }

  resetUser = () => {
    const user = { ...this.state.user }
    user.activated = true
    user.login = ''
    user.password = ''
    user.passwordConfirmation = ''
    user.firstName = ''
    user.lastName = ''
    user.email = ''
    user.contact = ''
    this.setState({
      user: user
    })
  }

  handleEditUser = (editionMode) => {
    const user = { ...this.state.user }
    const { selectedUser } = this.props
    user.activated = selectedUser.activated
    user.login = selectedUser.login ? selectedUser.login : ''
    user.firstName = selectedUser.firstName ? selectedUser.firstName : ''
    user.lastName = selectedUser.lastName ? selectedUser.lastName : ''
    user.email = selectedUser.email ? selectedUser.email : ''
    user.contact = selectedUser.contact ? selectedUser.contact : ''
    user.activated = selectedUser.activated
    user.rolesIds = selectedUser.roles && selectedUser.roles[0] ? [selectedUser.roles[0].id] : null
    user.positionId = selectedUser.position && selectedUser.position.id ? selectedUser.position.id : null
    user.password = ''
    user.passwordConfirmation = ''
    this.setState({
      user: user,
      editionMode: editionMode,
      isUserDetailsOpen: true,
      isDoubleClicked: false
    })
  }

  handleOnChangeInput = (value, stateName) => {
    let updatedUser = { ...this.state.user }
    updatedUser[stateName] = value
    this.setState({ user: updatedUser })
    this.validateBlur(updatedUser, stateName)
  }

  toggleChecked = () => {
    const user = { ...this.state.user }
    user.activated = !user.activated
    this.setState({
      user
    })
  }

  validateBlur = (updatedUser, stateName, validationType) => {
    let updatedError = { ...updatedUser }
    let error = false
    if (stateName === 'email') {
      error = this.validateEmail(updatedError.email)
    } else if (stateName === 'password') {
      if (updatedError.password !== '' && updatedError.passwordConfirmation !== '') {
        const isValidPassword = passwordRegex.test(updatedError.password)
        error = !isValidPassword
      } else if (updatedError.password === '' && updatedError.passwordConfirmation === '') {
        error = false
      }
    } else if (stateName === 'passwordConfirmation') {
      error = updatedError.passwordConfirmation !== updatedError.password
    } else {
      const containsSpecialSigns = regex.test(updatedError[stateName])
      error = updatedError[stateName].length < 1 || containsSpecialSigns
    }
    if (validationType === 'formValidation') {
      return error
    } else {
      updatedError[stateName + 'Error'] = error
      this.setState({ user: updatedError })
    }
  }

  validateEmail = (email) => {
    if (/^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(email)) {
      return false
    } return true
  }

  isFormValid = () => {
    let updatedUser = { ...this.state.user }
    return (
      (this.state.editionMode === 'editUser' ? true : !this.validateBlur(updatedUser, 'login', 'formValidation')) &&
      !this.validateBlur(updatedUser, 'firstName', 'formValidation') &&
      !this.validateBlur(updatedUser, 'lastName', 'formValidation') &&
      !this.validateBlur(updatedUser, 'email', 'formValidation') &&
      !this.validateBlur(updatedUser, 'contact', 'formValidation') &&
      !this.validateBlur(updatedUser, 'password', 'formValidation') &&
      !this.validateBlur(updatedUser, 'passwordConfirmation', 'formValidation')
    )
  }

  handleSelectedUser = (selectedUser, mode) => {
    this.props.getSelectedUser(selectedUser.id)
    if (mode === 'doubleClick') {
      this.setState({
        isUserDetailsOpen: true
      })
    }
  }

  saveUser = () => {
    const { user } = this.state
    if (this.state.editionMode === 'editUser') {
      const userId = this.state.selectedRow && this.state.selectedRow.id ? this.state.selectedRow.id : null
      const updatedUser = {
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email,
        contact: user.contact,
        activated: user.activated,
        rolesIds: user.rolesIds,
        positionId: user.positionId
      }
      this.props.putUser(updatedUser, userId)
      if (user.password !== '' && user.passwordConfirmation !== '') {
        this.props.putUserPassword(userId, { password: user.password })
      }
    } else {
      const updatedUser = {
        login: user.login,
        password: user.password,
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email,
        contact: user.contact,
        activated: user.activated,
        rolesIds: [1],
        positionId: 2
      }
      this.props.addUser(updatedUser)
    }
  }

  handleCloseEditUser = () => {
    const user = { ...this.state.user }
    user.activated = true
    user.login = ''
    user.password = ''
    user.passwordConfirmation = ''
    user.firstName = ''
    user.lastName = ''
    user.email = ''
    user.contact = ''
    this.setState({
      isUserDetailsOpen: false,
      selectedRow: null,
      user
    })
  }

  isSelectedUser = () => {
    if (this.state.selectedRow && this.state.selectedRow.id) {
      return this.state.selectedRow.id === this.props.selectedUser.id
    } else {
      return false
    }
  }

  render () {
    const columns = [
      {
        Header: 'User List',
        columns: [
          {
            Header: 'Id',
            accessor: 'id',
            width: 90
          },
          {
            Header: 'Login',
            accessor: 'login'
          },
          {
            Header: 'FirstName',
            accessor: 'firstName'
          },
          {
            Header: 'LastName',
            accessor: 'lastName'
          },
          {
            Header: 'Deleted',
            accessor: 'deleted'
          }
        ]
      }
    ]
    const {
      isUserDetailsOpen,
      selectedRow,
      usersOptions,
      user
    } = this.state
    const {
      login,
      firstName,
      lastName,
      email,
      contact,
      password,
      passwordConfirmation,
      activated
    } = user
    const isFormValid = this.isFormValid()
    const isSelectedUser = !this.isSelectedUser()
    return (
      <div className='users-container users-wrapper'>
        <div style={{ width: '100%', height: '100%' }}>
          {this.props.allUsersListLoading
          ? <div className='spinner-box'>
            <div className={'spinner'}>
              <Spinner
                style={{ margin: '200px auto', width: '50px' }}
                fadeIn='none'
                className={'spin-item'}
                color={'#fdb913'}
                name='ball-spin-fade-loader' />
            </div>
          </div>
          : <ReactTable
            data={usersOptions}
            columns={columns}
            multiSort
            showPagination
            minRows={0}
            getTdProps={(state, rowInfo) => {
              if (rowInfo && rowInfo.row) {
                return {
                  onClick: e => {
                    this.handleSelectedUser(rowInfo.original)
                    this.setState({
                      selectedRow: rowInfo.original,
                      isDoubleClicked: false
                    })
                  },
                  onDoubleClick: e => {
                    this.handleSelectedUser(rowInfo.original)
                    this.setState({
                      selectedRow: rowInfo.original,
                      isDoubleClicked: true
                    })
                  },
                  style: {
                    background: this.state.selectedRow
                      ? rowInfo.original.id ===
                        this.state.selectedRow.id
                        ? '#e5e5e5'
                        : ''
                      : '',
                    cursor: 'pointer'
                  }
                }
              }
            }} />}
        </div>
        <div className='users-btn-container'>
          <a href='admin-home'>
            <RaisedButton
              buttonStyle={{ width: '200px' }}
              backgroundColor='#244C7B'
              labelColor='#FCB636'
              label='Back'
              type='button' />
          </a>
          <RaisedButton
            onClick={() => this.handleAddUser('newUser')}
            buttonStyle={{ width: '200px', marginLeft: '25px' }}
            backgroundColor='#244C7B'
            labelColor='#FCB636'
            label='+ New'
            type='button' />
          <RaisedButton
            disabled={isSelectedUser}
            onClick={() => this.handleEditUser('editUser')}
            buttonStyle={{ width: '200px', marginLeft: '25px' }}
            backgroundColor='#244C7B'
            labelColor='#FCB636'
            label='Edit'
            type='button' />
        </div>
        {isUserDetailsOpen && this.props.isUserLoading
        ? <div className='users-spinner-fixed'>
          <Spinner
            style={{ margin: '200px auto', width: '50px' }}
            fadeIn='none'
            color={'#fdb913'}
            name='ball-spin-fade-loader' />
        </div>
        : <Dialog open={isUserDetailsOpen}>
          <div>
            <h2>User details</h2>
            <div className='user-info-save'>
              <p>{selectedRow && <span>ID: {selectedRow.id}</span>}</p>
              <p>{<Checkbox
                onClick={this.toggleChecked}
                checked={!activated} />} <p>Deleted</p></p>
              <RaisedButton
                onClick={this.saveUser}
                disabled={!isFormValid}
                buttonStyle={{ width: '200px' }}
                backgroundColor='#244C7B'
                labelColor='#FCB636'
                label='Save'
                type='button' />
            </div>
            <div>
              {this.state.editionMode !== 'editUser' && <TextField
                value={login}
                onChange={(e) => this.handleOnChangeInput(e.target.value, 'login')}
                errorText={user.loginError ? 'This field is required' : ''}
                fullWidth
                type='text'
                floatingLabelFixed
                floatingLabelText='Login' />}
              <TextField
                value={password}
                autoComplete='new-password'
                onChange={(e) => this.handleOnChangeInput(e.target.value, 'password')}
              // eslint-disable-next-line max-len
                errorText={user.passwordError ? 'Minimum 8 characters, min one uppercase letter, one lowercase letter, one number and one special character' : ''}
                floatingLabelText='Password'
                fullWidth
                floatingLabelFixed
                type='password'
                style={{ marginRight: '20px' }} />
              <TextField
                value={passwordConfirmation}
                autoComplete='new-password'
                onChange={(e) => this.handleOnChangeInput(e.target.value, 'passwordConfirmation')}
                errorText={user.passwordConfirmationError ? 'Required field. Both passwords must be the same.' : ''}
                floatingLabelText='Confirm Password'
                fullWidth
                floatingLabelFixed
                type='password' />
              <TextField
                value={firstName}
                onChange={(e) => this.handleOnChangeInput(e.target.value, 'firstName')}
                errorText={user.firstNameError ? 'This field is required' : ''}
                fullWidth
                floatingLabelFixed
                floatingLabelText='First Name' />
              <TextField
                value={lastName}
                onChange={(e) => this.handleOnChangeInput(e.target.value, 'lastName')}
                errorText={user.lastNameError ? 'This field is required' : ''}
                fullWidth
                floatingLabelFixed
                floatingLabelText='Last Name' />
              <TextField
                value={email}
                onChange={(e) => this.handleOnChangeInput(e.target.value, 'email')}
                errorText={user.emailError ? 'This field is required' : ''}
                fullWidth
                required
                type='email'
                floatingLabelFixed
                floatingLabelText='email' />
              <TextField
                value={contact}
                onChange={(e) => this.handleOnChangeInput(e.target.value, 'contact')}
                errorText={user.contactError ? 'This field is required' : ''}
                fullWidth
                floatingLabelFixed
                floatingLabelText='Contact(Phone)' />
              <FlatButton
                label='Cancel'
                secondary
                onClick={this.handleCloseEditUser} />
            </div>
          </div>
        </Dialog>}
      </div>
    )
  }
}

export default Users
