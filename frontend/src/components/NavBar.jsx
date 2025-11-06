import "../NavBar.css";

export const NavBar = () => {
  return (
    <nav className="nav">
      <h2>Log Analyzer</h2>
      <ul>
        <li className="active">
          <a href="/landing">Home</a>
        </li>
        <li>
          <a href="/account">Account</a>
        </li>
      </ul>
    </nav>
  );
};
