import{g as y,j as r,L as i}from"./index-PHBf0-X0.js";import{a as d}from"./axios-B4uVmeYG.js";const x=e=>document.cookie.replace(`/(?:(?:^|.*;s*)${e}s*=s*([^;]*).*$)|^.*$/`,"$1");var c={exports:{}},f="SECRET_DO_NOT_PASS_THIS_OR_YOU_WILL_BE_FIRED",g=f,T=g;function l(){}function h(){}h.resetWarningCache=l;var j=function(){function e(p,n,k,v,P,m){if(m!==T){var a=new Error("Calling PropTypes validators directly is not supported by the `prop-types` package. Use PropTypes.checkPropTypes() to call them. Read more at http://fb.me/use-check-prop-types");throw a.name="Invariant Violation",a}}e.isRequired=e;function t(){return e}var o={array:e,bigint:e,bool:e,func:e,number:e,object:e,string:e,symbol:e,any:e,arrayOf:t,element:e,elementType:e,instanceOf:t,node:e,objectOf:t,oneOf:t,oneOfType:t,shape:t,exact:t,checkPropTypes:h,resetWarningCache:l};return o.PropTypes=o,o};c.exports=j();var R=c.exports;const s=y(R);u.propTypes={to:s.string.isRequired,className:s.string,children:s.node.isRequired};function u({to:e,className:t,children:o}){const p=n=>{x("login")===""&&(alert("로그인이 필요합니다"),n.preventDefault())};return r.jsx(i,{to:e,className:t,onClick:n=>p(n),children:o})}function _(){const e=()=>{d.post("http://localhost:8080/api/example/test",{},{withCredentials:!0}).then(t=>{console.log(t)}).catch(t=>{console.log(t)})};return r.jsx(r.Fragment,{children:r.jsxs("main",{children:[r.jsx("h1",{children:"IntArear"}),r.jsx("p",{children:r.jsx(i,{to:"/login",children:"Login"})}),r.jsx("p",{children:r.jsx(i,{to:"/signup",children:"SignUp"})}),r.jsx("p",{children:r.jsx(u,{to:"/auth",className:"auth-test",children:"Auth Test"})}),r.jsx("button",{onClick:e,children:"Auth Test"})]})})}export{_ as default};
