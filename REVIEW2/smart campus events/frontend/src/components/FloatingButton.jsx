import { Plus } from "lucide-react";
import { motion } from "framer-motion";

export default function FloatingButton({ onClick }) {
  return (
    <motion.button
      onClick={onClick}
      className="fixed bottom-6 right-6 z-30 rounded-full bg-cyan-500 p-4 text-slate-900 shadow-lg transition hover:scale-110 hover:bg-cyan-400"
      animate={{ y: [0, -5, 0] }}
      transition={{ duration: 1.4, repeat: Infinity, ease: "easeInOut" }}
      whileTap={{ scale: 0.9 }}
    >
      <Plus />
    </motion.button>
  );
}
