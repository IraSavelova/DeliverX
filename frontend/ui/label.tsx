"use client";

import * as React from "react";
import * as LabelPrimitive from "@radix-ui/react-label";

function Label({
  className = "",
  ...props
}: React.ComponentProps<typeof LabelPrimitive.Root>) {
  const classes = [
    "flex items-center gap-2 text-sm leading-none font-medium select-none group-data-[disabled=true]:pointer-events-none group-data-[disabled=true]:opacity-50 peer-disabled:cursor-not-allowed peer-disabled:opacity-50",
    className
  ]
    .filter(Boolean)
    .join(" ");

  return (
    <LabelPrimitive.Root
      data-slot="label"
      className={classes}
      {...props}
    />
  );
}

export { Label };