import { Gem, Home, Menu, Wind, Search } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Info } from "lucide-react";
import { BookOpen } from "lucide-react";
import { NavItem } from "@/components/ui/nav-item";
import { ThemeToggle } from "./ThemeToggle";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "@/hooks/useAuth";
import { useUserRoutes } from "@/hooks/useRoleAuth";
import { UserDropdown } from "@/pages/auth/components/UserDropdown";
import { useState, useRef } from "react";
import { userService } from "@/services/userService";

// Define navigation links for different user states
const publicNavLinks = [
  { href: "/", label: "Trang chủ", icon: Home },
  { href: "/blog", label: "Blog", icon: BookOpen },
  { href: "/about", label: "Thông tin", icon: Info },
];

const memberNavLinks = [
  { href: "/", label: "Trang chủ", icon: Home },
  { href: "/blog", label: "Blog", icon: BookOpen },
  { href: "/plan", label: "Tiến trình", icon: Gem },
  { href: "/about", label: "Thông tin", icon: Info },
];

const coachNavLinks = [
  { href: "/blog", label: "Blog", icon: BookOpen },
];

export function Navbar() {
  const location = useLocation();
  const { isAuthenticated, user } = useAuth();
  const { canAccessPlan, canAccessCoach, canAccessAdmin, canAccessContentAdmin } = useUserRoutes();
  const [search, setSearch] = useState("");
  const [results, setResults] = useState<Array<{
    userId: string;
    username: string;
    email: string;
    profilePicture?: string;
  }>>([]);
  const [showDropdown, setShowDropdown] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const inputRef = useRef<HTMLInputElement>(null);

  // Determine which nav links to show
  const getNavLinks = () => {
    if (!isAuthenticated) return publicNavLinks;

    if (!user) return publicNavLinks;

    // Admin and Content Admin should only see dashboard
    if (canAccessAdmin || canAccessContentAdmin) {
      return []; // No public nav links for admins
    }

    // Coach can only see blog
    if (canAccessCoach) {
      return coachNavLinks;
    }

    // For members (normal/premium)
    if (canAccessPlan) return memberNavLinks;

    // Default fallback
    return publicNavLinks;
  };
  const navLinks = getNavLinks();

  const handleSearchChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setSearch(value);
    if (value.trim().length < 2) {
      setResults([]);
      setShowDropdown(false);
      return;
    }
    setLoading(true);
    try {
      const users = await userService.searchUsers(value);
      setResults(users);
      setShowDropdown(true);
    } catch {
      setResults([]);
      setShowDropdown(false);
    } finally {
      setLoading(false);
    }
  };

  const handleSearchButton = () => {
    if (search.trim().length >= 2) {
      inputRef.current?.focus();
      setLoading(true);
      userService.searchUsers(search).then(users => {
        setResults(users);
        setShowDropdown(true);
      }).catch(() => {
        setResults([]);
        setShowDropdown(false);
      }).finally(() => setLoading(false));
    } else {
      inputRef.current?.focus();
    }
  };

  const handleSelectUser = (userId: string) => {
    setSearch("");
    setShowDropdown(false);
    navigate(`/user/${userId}`);
  };

  return (
    <header className="fixed top-0 left-0 right-0 z-50 bg-white/95 dark:bg-slate-900/95 border-b-2 border-emerald-200 dark:border-slate-700 backdrop-blur-xl shadow-lg shadow-emerald-100/50 dark:shadow-slate-900/50">
      <div className="max-w-1xl mx-auto px-6 flex items-center justify-between h-18">
        <div className="flex items-center gap-3 text-2xl font-black text-slate-800 dark:text-white">
          <Wind className="h-8 w-8 text-emerald-500" />
          <Link to="/" className="text-xl font-bold">
            QuitTogether
          </Link>
        </div>

        <nav className="hidden md:flex items-center gap-6">
          {navLinks.map((item) => (
            <NavItem
              key={item.href}
              href={item.href}
              icon={item.icon}
              isActive={item.href === location.pathname}
            >
              {item.label}
            </NavItem>
          ))}
        </nav>
        <div className="flex items-center gap-3">
          <ThemeToggle />
          {/* Authenticated User Menu */}
          {isAuthenticated && user ? (
            <UserDropdown />
          ) : (
            /* Guest User Buttons */
            <>
              <Link
                to="/login"
                className="hidden md:flex items-center px-4 py-2.5 rounded-xl font-semibold text-sm border-2 border-emerald-200 dark:border-slate-600 text-slate-700 dark:text-slate-300 bg-gradient-to-r from-emerald-50 to-emerald-100 dark:from-slate-700 dark:to-slate-600 hover:border-emerald-300 dark:hover:border-emerald-500 hover:scale-105 transition-all duration-300 hover:-translate-y-0.5 hover:shadow-lg"
              >
                Đăng nhập
              </Link>
              <Link
                to="/register"
                className="hidden md:flex items-center px-4 py-2.5 rounded-xl font-bold text-sm text-white bg-gradient-to-r from-emerald-500 to-emerald-600 hover:from-emerald-600 hover:to-emerald-700 hover:scale-105 transition-all duration-300 hover:-translate-y-0.5 shadow-lg shadow-emerald-200/50 dark:shadow-emerald-500/25"
              >
                Đăng ký
              </Link>
            </>
          )}
          {/* Mobile Menu */}
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" size="icon" className="md:hidden">
                <Menu className="h-5 w-5" />
                <span className="sr-only">Toggle menu</span>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              {navLinks.map((item) => (
                <DropdownMenuItem key={item.href} asChild>
                  <Link to={item.href}>{item.label}</Link>
                </DropdownMenuItem>
              ))}
              <DropdownMenuSeparator />
              {!isAuthenticated && (
                <>
                  <DropdownMenuItem asChild>
                    <Link to="/login">Đăng nhập</Link>
                  </DropdownMenuItem>
                  <DropdownMenuItem asChild>
                    <Link to="/register">Đăng ký</Link>
                  </DropdownMenuItem>
                </>
              )}
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </div>
    </header>
  );
}
