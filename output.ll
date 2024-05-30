; ModuleID = 'module'
source_filename = "module"

define i32 @main() {
mainEntry:
  %a = alloca i32, align 4
  store i32 0, i32* %a, align 4
  %count = alloca i32, align 4
  store i32 0, i32* %count, align 4
  %a1 = load i32, i32* %a, align 4
  %addtmp = add i32 %a1, 1
  %finalcmp = icmp ne i32 0, %addtmp
  %zexttmp = zext i1 %finalcmp to i32
  %cmptmp = icmp sgt i32 %zexttmp, 1
  %zexttmp2 = zext i1 %cmptmp to i32
  %finalcmp3 = icmp ne i32 0, %zexttmp2
  br i1 %finalcmp3, label %true0, label %false0

true0:                                            ; preds = %mainEntry
  %count4 = load i32, i32* %count, align 4
  ret i32 %count4
  br label %end0

false0:                                           ; preds = %mainEntry
  br label %end0

end0:                                             ; preds = %false0, %true0
  ret i32 0
}
