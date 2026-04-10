"use client";

import * as React from "react";
import * as TabsPrimitive from "@radix-ui/react-tabs";

function Tabs({
  className = "",
  ...props
}: React.ComponentProps<typeof TabsPrimitive.Root>) {
  const classes = ["flex flex-col gap-2", className]
    .filter(Boolean)
    .join(" ");

  return (
    <TabsPrimitive.Root
      data-slot="tabs"
      className={classes}
      {...props}
    />
  );
}

function TabsList({
  className = "",
  ...props
}: React.ComponentProps<typeof TabsPrimitive.List>) {
  const classes = [
    "bg-muted text-muted-foreground inline-flex h-9 w-fit items-center justify-center rounded-xl p-[3px] flex",
    className
  ]
    .filter(Boolean)
    .join(" ");

  return (
    <TabsPrimitive.List
      data-slot="tabs-list"
      className={classes}
      {...props}
    />
  );
}

function TabsTrigger({
  className = "",
  ...props
}: React.ComponentProps<typeof TabsPrimitive.Trigger>) {
  const classes = [
    "data-[state=active]:bg-card dark:data-[state=active]:text-foreground focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:outline-ring dark:data-[state=active]:border-input dark:data-[state=active]:bg-input/30 text-foreground dark:text-muted-foreground inline-flex h-[calc(100%-1px)] flex-1 items-center justify-center gap-1.5 rounded-xl border border-transparent px-2 py-1 text-sm font-medium whitespace-nowrap transition-[color,box-shadow] focus-visible:ring-[3px] focus-visible:outline-1 disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg]:shrink-0 [&_svg:not([class*='size-'])]:size-4",
    className
  ]
    .filter(Boolean)
    .join(" ");

  return (
    <TabsPrimitive.Trigger
      data-slot="tabs-trigger"
      className={classes}
      {...props}
    />
  );
}

function TabsContent({
  className = "",
  ...props
}: React.ComponentProps<typeof TabsPrimitive.Content>) {
  const classes = ["flex-1 outline-none", className]
    .filter(Boolean)
    .join(" ");

  return (
    <TabsPrimitive.Content
      data-slot="tabs-content"
      className={classes}
      {...props}
    />
  );
}

export { Tabs, TabsList, TabsTrigger, TabsContent };