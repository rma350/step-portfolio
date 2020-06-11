// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Gets the comments from the server.
 */
async function getCommentData() {
  const response = await fetch('/data');
  const commentList = await response.json();
  return commentList;
}

function updateComments(commentList) {
  const commentContainer = document.getElementById('comment_list');
  commentContainer.innerHTML='';
  for(let c = 0; c < commentList.length; c++) {
    const comment = commentList[c];
    const commentTime = Date(comment['timestamp']).toString();
    li = document.createElement('li');
    commentP = document.createElement('p');
    emailP = document.createElement('p');
    timeP = document.createElement('p');    
    commentP.appendChild(document.createTextNode(comment['comment']));
    emailP.appendChild(document.createTextNode(comment['userEmail']));
    timeP.appendChild(document.createTextNode(commentTime));
    li.appendChild(commentP);
    li.appendChild(emailP);
    li.appendChild(timeP);
    commentContainer.appendChild(li);
  }
}

async function loginInfo() {
  const response = await fetch('/login');
  const loginStatus = await response.json();
  return loginStatus;
}

function disableNewComments() {
  const commentText = document.getElementById('new_comment_text');
  commentText.setAttribute('disabled', 'true');
  commentText.value = 'Log in to leave a commment.';
  const submitButton = document.getElementById('new_comment_submit');
  submitButton.setAttribute('disabled', 'true');
}

function updateChangeLoginBox(loginStatus) {
  const logInState = document.getElementById('login_state');
  const changeLogInState = document.getElementById('change_login_state');
  logInState.innerHTML = '';
  changeLogInState.innerHTML = '';
  stateMessage = 'Logged out';
  actionMessage = 'Log in'
  changeUrl = loginStatus['changeLogInUrl'];
  if(loginStatus['isLoggedIn']) {
    stateMessage = 'Logged in as: ' + loginStatus['userEmail'];
    actionMessage = 'Log out';
  }
  logInState.appendChild(document.createTextNode(stateMessage));
  changeLogInState.appendChild(document.createTextNode(actionMessage));
  changeLogInState.setAttribute('href', changeUrl);
}

async function runOnLoad() {
  // TODO(rander): these can run in parallel
  const loginStatus = await loginInfo();
  const commentData = await getCommentData();
  if(!loginStatus['isLoggedIn']) {
    disableNewComments();
  }
  updateChangeLoginBox(loginStatus);
  updateComments(commentData);
}
