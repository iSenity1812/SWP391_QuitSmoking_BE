import { Link } from "react-router-dom";
import { type LucideIcon } from "lucide-react";

export function NavItem({
  href,
  icon: Icon,
  children,
  isActive,
}: {
  href: string;
  icon: LucideIcon;
  children: React.ReactNode;
  isActive?: boolean;
}) {
  return (
    <Link
      to={href}
      // Inside src/components/ui/nav-item.tsx, within the NavItem component's return statement:
      className={`flex items-center gap-2 text-sm font-bold text-slate-600 dark:text-slate-300 px-4 py-2 rounded-lg transition-all duration-300 ease-in-out hover:text-emerald-600 dark:hover:text-emerald-400 hover:bg-gradient-to-r hover:from-emerald-50 hover:to-emerald-100 dark:hover:from-emerald-900/20 dark:hover:to-emerald-800/20 hover:shadow-md hover:scale-[1.02] hover:-translate-y-0.5 ${isActive
        ? "text-emerald-600 dark:text-emerald-400 shadow-lg shadow-emerald-200/50" // Current active classes
        : "text-slate-600 dark:text-slate-300 hover:text-emerald-600 dark:hover:text-emerald-400 hover:bg-gradient-to-r hover:from-emerald-50 hover:to-emerald-100 dark:hover:from-emerald-900/20 dark:hover:to-emerald-800/20"
        }`}
    >
      <Icon className="h-4 w-4" />
      {children}
    </Link>
  );
}
