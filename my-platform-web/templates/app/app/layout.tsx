import type { Metadata } from "next";
import { AppShell } from "@/layouts/AppShell";
import "./globals.css";

export const metadata: Metadata = {
	title: "__TITLE__",
	description: "__TITLE__",
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
	return (
		<html lang="ko">
			<body>
				<AppShell>{children}</AppShell>
			</body>
		</html>
	);
}
